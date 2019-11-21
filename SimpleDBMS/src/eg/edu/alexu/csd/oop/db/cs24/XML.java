package eg.edu.alexu.csd.oop.db.cs24;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class XML {

    private static XML xml;

    private XML() {    }

    public static XML getInstace() {
        if(xml == null) {
            xml = new XML();
        }
        return xml;
    }

    public void SaveTable(Table table, String path)
    {
        Document dom;
        Element e=null ;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();
            Element rootEle = dom.createElement(table.getName());
            ArrayList<Column<?>> columns=table.getColumns();
            for(int i=0;i<columns.size();++i)
            {
                e = dom.createElement(columns.get(i).getClass().getName());
                e.setAttribute("id",columns.get(i).getName());
                if(columns.get(i).getType().getSimpleName().equals("String"))
                    e.setAttribute("type","varchar");
                else
                    e.setAttribute("type","int");
                rootEle.appendChild(e);
            }
            dom.appendChild(rootEle);
            dbf.setValidating(true);
            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(
                        OutputKeys.DOCTYPE_SYSTEM, path.substring(path.lastIndexOf('\\')+1,path.indexOf(".xml"))+".dtd");
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(path)));
                CreateDTD(table,path);
            } catch (TransformerException | IOException te) {
                System.out.println(te.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }

    private void CreateDTD(Table table,String path)
    {
        //http://edutechwiki.unige.ch/en/DTD_tutorial
        try {
            path=path.replace("xml","dtd");
            File ff = new File(path);
            System.out.println(ff.createNewFile());
            FileWriter fw = new FileWriter(ff);
            fw.write("<!ELEMENT " + table.getName() + " ("+table.getColumns().getClass().getName()+"+)>\n");
            fw.write("<!ELEMENT"+table.getColumns().getClass().getName()+" (Data?)>\n");
            fw.write("<!ATTLIST"+table.getColumns().getClass().getName()+ " id ID #REQUIRED\n");
            fw.write("<!ATTLIST"+table.getColumns().getClass().getName()+ " type #REQUIRED\n");
            fw.write("<!ELEMENT Data (#PCDATA)>\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
	public Table LoadTable(String path)
    {
        Table table=null;
        try {
            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            HashMap<String,String> ColumnsData=new HashMap<>();
            NodeList nList = doc.getElementsByTagName(Column.class.getName());
            for(int i=0;i<nList.getLength();++i)
            {
                Node node=nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) node;
                    ColumnsData.put(eElement.getAttribute("id"),eElement.getAttribute("type"));
                }
            }
            table=new Table(root.getNodeName(),ColumnsData);
            for(HashMap.Entry<String,String> entry: ColumnsData.entrySet())
            {
                Column<?>column=table.getColumnByName(entry.getKey());
                nList = doc.getElementById(entry.getKey()).getChildNodes();
                for (int i = 0; i < nList.getLength(); ++i)
                {
                    Node node = nList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element eElement = (Element) node;
                        if(entry.getValue().equals("int"))
                            ((Column<Integer>)column).add(Integer.parseInt(eElement.getTextContent()));
                        else
                        	((Column<String>)column).add(eElement.getTextContent());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
}