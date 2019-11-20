package eg.edu.alexu.csd.oop.db.cs24;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Table {
	
	ConditionParser cp = ConditionParser.getInstance();
	
	private String name;
	private ArrayList<Column<?>> columns;
	
	@SuppressWarnings("unchecked")
	public Table(String name, HashMap<String, String> columns) {
		this.name = name;
		this.columns = new ArrayList<Column<?>>();
		
		Set<?> set = columns.entrySet();
		Iterator<?> iter = set.iterator();
		
		while (iter.hasNext()) {
			Map.Entry<String, String> m = (Map.Entry<String, String>) iter.next();
			if(m.getValue().equals("int")) {
				this.columns.add(new Column<Integer>(m.getKey()));
			}else {
				this.columns.add(new Column<String>(m.getKey()));
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Column<?>> getColumns() {
		return columns;
	}
	
	@SuppressWarnings("unchecked")
	public void addRecord(HashMap<String, String> record , String path) {
		
		Document doc = getDocument(path);
		
		Set<?> set = record.entrySet();
		Iterator<?> iter = set.iterator();
		
		try {
			while (iter.hasNext()) {
				Map.Entry<String, String> m = (Map.Entry<String, String>) iter.next();
				// add in xml file
				Node column = doc.getElementById(m.getKey());
				Element newElement = (Element) doc.appendChild(doc.createTextNode(m.getValue()));
				column.appendChild(newElement);
				// add in the table itself
				Column<?> col = getColumnByName(m.getKey());
				if(col.getType().getSimpleName().equals("Integer")) {
					((Column<Integer>)col).add(Integer.parseInt(m.getValue()));
				}else {
					((Column<String>)col).add(m.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void updateRecord(HashMap<String, String> colValues, ArrayList<String> condition, String path) {

		Document doc = getDocument(path);
		
		Set<?> set = colValues.entrySet();
		Iterator<?> iter = set.iterator();
		
		ArrayList<String> colsNeeded = getColsNeeded(condition);
		ArrayList<String> reps = new ArrayList<String>();
		
		while(iter.hasNext()) {
			Map.Entry<String, String> m = (Map.Entry<String, String>) iter.next();
			// update from xml
			Node column = doc.getElementById(m.getKey());
			NodeList colList = column.getChildNodes();
			for (int i = 0; i < colList.getLength(); i++) {
				for (int j = 0; j < reps.size(); i++) {
					reps.add((getColumnByName(colsNeeded.get(j)).getElements().get(i)).toString());
				}
				if(cp.evaluate(condition, reps)) {
					colList.item(i).setTextContent(m.getValue());
				}
			}
			// update in the table itself
			Column<?> col = getColumnByName(m.getKey());
			ArrayList<?> elements = col.getElements();
			for (Iterator<?> iterator = elements.iterator(); iterator.hasNext();) {
				for (int i = 0; i < reps.size(); i++) {
					reps.add((getColumnByName(colsNeeded.get(i)).getElements().get(elements.indexOf(iterator.next()))).toString());
				}
				if(cp.evaluate(condition, reps)) {
					if(col.getType().getSimpleName().equals("Integer")) {
						((Column<Integer>)col).set(elements.indexOf(iterator.next()),Integer.parseInt(m.getValue()));
					}else {
						((Column<String>)col).set(elements.indexOf(iterator.next()),m.getValue());
					}
				}
			}
		}
	}
		
	private Column<?> getColumnByName(String name) {
		for (Column<?> column : this.columns) {
			if(column.getName().equals(name)) {
				return column;
			}
		}
		return null;
	}
	
	private Document getDocument(String path) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(path);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	private ArrayList<String> getColsNeeded(ArrayList<String> condition) {
		ArrayList<String> cols = new ArrayList<String>();
		for (int i = 0; i < (condition.size() + 1)/4; i++) {
			cols.add(condition.get((3 * i) + i));
		}
		return cols;
	}
	
}
