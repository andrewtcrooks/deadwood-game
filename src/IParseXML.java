import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import org.xml.sax.SAXException;

/**
 * Represents an interface for parsing XML files.
 */
public interface IParseXML {
    Document getDocFromFile(String filename) throws ParserConfigurationException, IOException, SAXException;
    void readData(Document d);
}
