package eg.edu.alexu.csd.oop.db.cs24;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
				this.columns.add(new Column<Integer>(m.getKey(), Integer.class));
			}else {
				this.columns.add(new Column<String>(m.getKey(), String.class));
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
				Element DataCell = doc.createElement("Data");
				DataCell.appendChild(doc.createTextNode(m.getValue()));
				column.appendChild(DataCell);
				// add in the table itself
				Column<?> col = getColumnByName(m.getKey());
				if(col.getType().getSimpleName().equals("Integer")) {
					((Column<Integer>)col).add(Integer.parseInt(m.getValue()));
				}else {
					((Column<String>)col).add(m.getValue());
				}
			}
			writeInFile(path, doc);
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
				reps.clear();
				for (int j = 0; j < colsNeeded.size(); j++) {
					reps.add((getColumnByName(colsNeeded.get(j)).getElements().get(i)).toString());
				}
				if(cp.evaluate((ArrayList<String>) condition.clone(), reps)) {
					colList.item(i).setTextContent(m.getValue());
				}
			}
			// update in the table itself
			Column<?> col = getColumnByName(m.getKey());
			ArrayList<?> elements = col.getElements();
			for(Object column1 : elements) {
				reps.clear();
				for (int i = 0; i < colsNeeded.size(); i++) {
					reps.add((getColumnByName(colsNeeded.get(i)).getElements().get(elements.indexOf(column1))).toString());
				}
				if(cp.evaluate((ArrayList<String>) condition.clone(), reps)) {
					if(col.getType().getSimpleName().equals("Integer")) {
						((Column<Integer>)col).set(elements.indexOf(column1),Integer.parseInt(m.getValue()));
					}else {
						((Column<String>)col).set(elements.indexOf(column1),m.getValue());
					}
				}
			}
		}
		writeInFile(path, doc);
	}
	
	@SuppressWarnings("unchecked")
	public void deleteRecord(ArrayList<String> condition, String path) {
		
		Document doc = getDocument(path);
		ArrayList<String> colsNeeded = getColsNeeded(condition);
		ArrayList<String> reps = new ArrayList<String>();
		
		int count = this.columns.get(0).getElements().size();
		
		for (int i = 0; i < count; i++) {
			reps.clear();
			for (int j = 0; j < colsNeeded.size(); j++) {
				reps.add((getColumnByName(colsNeeded.get(j)).getElements().get(i)).toString());
			}
			if(cp.evaluate((ArrayList<String>) condition.clone(), reps)) {				
				for (int j = 0; j < this.columns.size(); j++) {
					// delete from xml
					Node col = doc.getElementById(this.columns.get(j).getName());
					NodeList colList = col.getChildNodes();
					Element e = (Element) colList.item(i);
					e.getParentNode().removeChild(e);
					// delete from table itself
					this.columns.get(j).getElements().remove(i);
				}
				count--;
			}
		}
		writeInFile(path, doc);
	}
	
	/**
	 * @param path
	 * @param doc
	 * @throws TransformerFactoryConfigurationError
	 */
	private void writeInFile(String path, Document doc) throws TransformerFactoryConfigurationError {
		try {
		    Transformer tr = TransformerFactory.newInstance().newTransformer();
		    tr.setOutputProperty(
		            OutputKeys.DOCTYPE_SYSTEM, path.substring(path.lastIndexOf('\\')+1,path.indexOf(".xml"))+".dtd");
		    tr.transform(new DOMSource(doc),
		            new StreamResult(new FileOutputStream(path)));
		} catch (TransformerException | IOException te) {
		    System.out.println(te.getMessage());
		}
	}
		
	public Column<?> getColumnByName(String name) {
		for (Column<?> column : this.columns) {
			if(column.getName().equals(name)) {
				return column;
			}
		}
		return null;
	}
	
	private Document getDocument(String path) {
		Document doc=null;
		try {
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
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
