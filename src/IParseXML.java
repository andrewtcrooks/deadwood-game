import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import org.xml.sax.SAXException;

public interface IParseXML {
    Document getDocFromFile(String filename) throws ParserConfigurationException, IOException, SAXException;
    void readData(Document d);
}
