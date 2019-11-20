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
import java.io.FileOutputStream;
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

    public void TableParse(Table table, String path)
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
                    Element DataInCell=dom.createElement(String.valueOf(elements.get(j)));
                    NumberOfCell.appendChild(DataInCell);
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
                        OutputKeys.DOCTYPE_SYSTEM, path.substring(path.lastIndexOf('\\'))+".dtd");
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(path)));
            } catch (TransformerException | IOException te) {
                System.out.println(te.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }

}
