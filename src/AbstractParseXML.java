import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import java.io.IOException;

public abstract class AbstractParseXML {
    /**
     * Returns a Document object from the given filename.
     * @param filename
     * @return Document
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public Document getDocFromFile(String filename) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(filename);
        return doc;
    }


    // Abstract methods

    /**
     * Reads the data from the given Document object.
     * @param d
     */
    public abstract void readData(Document d);
}
