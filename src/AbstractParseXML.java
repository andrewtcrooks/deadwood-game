import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.io.InputStream;

/**
 * Represents an abstract class for parsing XML files.
 * 
 * This class provides a method for getting a Document object from a given filename.
 * This class also provides an abstract method for reading data from a Document object.
 */
public abstract class AbstractParseXML {
    /**
     * Returns a Document object from the given filename.
     * @param filename
     * @return Document
     * @throws Exception
     */
    Document getDocFromFile(String filename) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        if (is == null) {
            throw new IllegalArgumentException("Could not find file " + filename);
        }
        Document doc = db.parse(is);
        return doc;
    }


    // Abstract methods

    /**
     * Reads the data from the given Document object.
     * @param d
     */
    abstract void readData(Document d) throws Exception;
}
