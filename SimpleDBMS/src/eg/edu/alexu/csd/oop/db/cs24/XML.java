package eg.edu.alexu.csd.oop.db.cs24;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
                e = dom.createElement(columns.getClass().getName());
                e.setAttribute("id",columns.get(i).getName());
                ArrayList<?>elements=columns.get(i).getElements();
                for(int j=0;j<elements.size();++j)
                {
                    Element NumberOfCell=dom.createElement(String.valueOf(j));
                    NumberOfCell.appendChild(dom.createTextNode(elements.get(j).toString()));
                    e.appendChild(NumberOfCell);
                }

            }
            rootEle.appendChild(e);
            dom.appendChild(rootEle);
            dbf.setValidating(true);
            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(
                        OutputKeys.DOCTYPE_SYSTEM, path.substring(path.lastIndexOf('\\'),path.indexOf(".xml"))+".dtd");
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
            path.replace("xml","dtd");
            int MaxNumberOfRows=0;
            File ff = new File(path);
            System.out.println(ff.createNewFile());
            FileWriter fw = new FileWriter(ff);
            fw.write("<!ELEMENT " + table.getName() + "("+table.getColumns().getClass().getName()+"+)>\n");
            ArrayList<Column<?>> columns=table.getColumns();
            for(int i=0;i<columns.size();++i)
            {
                fw.write("<!ELEMENT"+table.getColumns().getClass().getName()+"(");
                ArrayList<?>elements=columns.get(i).getElements();
                for(int j=0;j<elements.size();++j)
                {
                   fw.write(j);
                   MaxNumberOfRows=Math.max(MaxNumberOfRows,j);
                   if(j!=elements.size()-1)
                       fw.write(",");
                }
                fw.write(")>\n");
            }
            fw.write("<!ATTLIST"+table.getColumns().getClass().getName()+ "id ID #REQUIRED");
            for(int i=0;i<=MaxNumberOfRows;++i)
                fw.write("<!ELEMENT"+i+"(#PCDATA)");
            fw.write(")>\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
