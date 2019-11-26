package eg.edu.alexu.csd.oop.db.cs24;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

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
	
	private XML parseXML;
	private Document doc;
	
	private String name;
	private ArrayList<Column<?>> columns;
	private String path = "";

	public Table(String name, ArrayList<String> columns) {
		this.name = name;
		this.columns = new ArrayList<Column<?>>();
		
		for(int i = 0; i < columns.size(); i++) {
			if(columns.get(i).contains("int")) {
				this.columns.add(new Column<Integer>(columns.get(i).split(", ")[0], Integer.class));
			}else {
				this.columns.add(new Column<String>(columns.get(i).split(", ")[0], String.class));
			}
		}
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}
	
	public ArrayList<Column<?>> getColumns() {
		return columns;
	}
	
	public void setDoc(Document newDoc) {
		this.doc.setTextContent(newDoc.getTextContent());
	}
	
	public void createXML() {
		parseXML = XML.getInstace();
		parseXML.SaveTable(this, path);
		doc = getDocument();
	}
	
	@SuppressWarnings("unchecked")
	public int addRecord(HashMap<String, String> record) {

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
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int updateRecord(HashMap<String, String> colValues, ArrayList<String> condition) {

		Set<?> set = colValues.entrySet();
		Iterator<?> iter = set.iterator();
		
		ArrayList<String> colsNeeded = getColsNeeded(condition);
		ArrayList<String> reps = new ArrayList<String>();
		
		int change = 0;
		
		while(iter.hasNext()) {
			Map.Entry<String, String> m = (Map.Entry<String, String>) iter.next();
			Node column = doc.getElementById(m.getKey());
			NodeList colList = column.getChildNodes();
			Column<?> col = getColumnByName(m.getKey());
			ArrayList<?> elements = col.getElements();
			for (int i = 0; i < colList.getLength(); i++) {
				reps.clear();
				if (condition != null) {
					for (int j = 0; j < colsNeeded.size(); j++) {
						reps.add((getColumnByName(colsNeeded.get(j)).getElements().get(i)).toString());
					} 
				}
				if(condition == null || cp.evaluate((ArrayList<String>) condition.clone(), reps)) {
					change++;
					// update from xml
					colList.item(i).setTextContent(m.getValue());
					// update in the table itself
					if(col.getType().getSimpleName().equals("Integer")) {
						((Column<Integer>)col).set(elements.indexOf(elements.get(i)),Integer.parseInt(m.getValue()));
					}else {
						((Column<String>)col).set(elements.indexOf(elements.get(i)),m.getValue());
					}
				}
			}
		}
		return change / colValues.size();
	}
	
	@SuppressWarnings("unchecked")
	public int deleteRecord(ArrayList<String> condition) {

		ArrayList<String> colsNeeded = getColsNeeded(condition);
		ArrayList<String> reps = new ArrayList<String>();

		int count = this.columns.get(0).getElements().size();
		int change = 0;

		for (int i = 0; i < count; i++) {
			reps.clear();
			if (condition != null) {
				for (int j = 0; j < colsNeeded.size(); j++) {
					reps.add((getColumnByName(colsNeeded.get(j)).getElements().get(i)).toString());
				} 
			}
			if(condition == null || cp.evaluate((ArrayList<String>) condition.clone(), reps)) {
				change++;
				for (int j = 0; j < this.columns.size(); j++) {
					// delete from xml
					Node col = doc.getElementById(this.columns.get(j).getName());
					NodeList colList = col.getChildNodes();
					Element e = (Element) colList.item(i);
					e.getParentNode().removeChild(e);
					// delete from table itself
					this.columns.get(j).getElements().remove(i);
				}
				i--;
				count--;
			}
		}
		return change;
	}

	@SuppressWarnings("unchecked")
	public Object[][] SelectRecord(ArrayList<String> condition, ArrayList<String> ColumnNames) {

		ArrayList<String> colsNeeded = getColsNeeded(condition);
		ArrayList<String> reps = new ArrayList<String>();

		int index=0;
		ArrayList<ArrayList<Object>>answer=new ArrayList<>();
		for (int i = 0; i < this.columns.get(0).getElements().size(); ++i) {
			reps.clear();
			if (condition != null) {
				for (String s : colsNeeded) {
					reps.add((getColumnByName(s).getElements().get(i)).toString());
				} 
			}
			if(condition == null || cp.evaluate((ArrayList<String>) condition.clone(), reps)) {
				answer.add(new ArrayList<>());
				for (int j = 0; j < this.columns.size(); j++) {
					Node col = doc.getElementById(this.columns.get(j).getName());
					NodeList colList = col.getChildNodes();
					Element e = (Element) colList.item(i);
					if(e.getParentNode().getAttributes().item(0).toString().equals("int"))
						answer.get(index).add(Integer.parseInt(e.getTextContent()));
					else
						answer.get(index).add(e.getTextContent());
				}
				++index;
			}
		}
		ArrayList<Integer> Indices=new ArrayList<Integer>();
		for (String columnName : ColumnNames)
			Indices.add(this.columns.indexOf(getColumnByName(columnName)));
		
		if (answer.size() != 0) {
			Object[][] AnswerArray = new Object[answer.size()][Indices.size()];
			for (int i = 0; i < Indices.size(); i++) {
				for (int j = 0; j < answer.size(); j++) {
					AnswerArray[j][i] = answer.get(j).get(Indices.get(i));
				}
			} 
			return AnswerArray;
		}
		return null;
	}
	
	/**
	 * @param path
	 * @param doc
	 * @throws TransformerFactoryConfigurationError
	 */
	public void writeInFile() throws TransformerFactoryConfigurationError {
		try {
		    Transformer tr = TransformerFactory.newInstance().newTransformer();
		    tr.setOutputProperty(
		            OutputKeys.DOCTYPE_SYSTEM, path.substring(path.lastIndexOf('\\')+1,path.indexOf(".xml"))+".dtd");
            StreamResult str=new StreamResult(new FileOutputStream(path));
            tr.transform(new DOMSource(doc),str);
            str.getOutputStream().close();
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
	
	private Document getDocument() {
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
		if(condition==null)
			return null;
		for (int i = 0; i < (condition.size() + 1)/4; i++) {
			cols.add(condition.get((3 * i) + i));
		}
		return cols;
	}
	
	public Table clone() {
		ArrayList<String> colNames = new ArrayList<String>();
		for (int i = 0; i < this.columns.size(); i++) {
			if(this.columns.get(i).getType().getSimpleName().equals("Integer")) {
				colNames.add(this.columns.get(i).getName() + ", int");
			}else {
				colNames.add(this.columns.get(i).getName() + ", varchar");
			}
		}
		Table t = new Table(this.name, colNames);		
		t.setPath(this.path);
		return t;
	}
	
}
