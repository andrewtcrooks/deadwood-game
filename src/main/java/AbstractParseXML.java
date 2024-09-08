import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Document;
import java.io.InputStream;

/**
 * Represents an abstract class for parsing XML files.
 * <p>
 * This class provides abstract methods for reading data from a Document object.
 * This class also provides shared methods for parsing XML files.
 */
public abstract class AbstractParseXML {


// Abstract Methods


    /**
     * Reads and returns a NodeList object of the Element objects from the given filename.
     */
    public abstract NodeList readData(String fileName) throws Exception;

    
// Shared methods for ParseBoardXML and ParseCardsXML


    /**
     * Returns a Document object from the given filename.
     * @param filename
     * @return Document
     * @throws Exception
     */
    public Document getDocFromFile(String filename) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        if (is == null) {
            throw new IllegalArgumentException("Could not find file " + filename);
        }
        Document doc = db.parse(is);
        return doc;
    }
        
    /**
     * Parses the name from the given Element object.
     * 
     * @param element
     * @return String
     */
    public String parseName(Element element) {
        return element.getAttribute("name");
    }

    /**
     * Parses the area from the given Element object.
     * 
     * @param areaElement
     * @return Area
     */
    public Area parseArea(Element areaElement) {
        int x = Integer.parseInt(areaElement.getAttribute("x"));
        int y = Integer.parseInt(areaElement.getAttribute("y"));
        int h = Integer.parseInt(areaElement.getAttribute("h"));
        int w = Integer.parseInt(areaElement.getAttribute("w"));
        return new Area(x, y, h, w);
    }

    /**
     * Parses roles from the given Element object.
     * 
     * @param parentElement
     * @return List<Role>
     */
    public List<Role> parseRoles(Element parentElement, boolean isCard) {
        List<Role> roles = new ArrayList<>();
        NodeList partsList = parentElement.getElementsByTagName("part");
        for (int j = 0; j < partsList.getLength(); j++) {
            Element partElement = (Element) partsList.item(j);
            roles.add(parseRole(partElement, isCard));
        }
        return roles;
    }

    /**
     * Parses a role from the given Element object.
     * 
     * @param partElement
     * @return Role
     */
    public Role parseRole(Element partElement, boolean isCard) {
        String roleName = partElement.getAttribute("name");
        int roleLevel = Integer.parseInt(partElement.getAttribute("level"));
        // Parse the Area
        Element areaElement = (Element) partElement.getElementsByTagName("area").item(0);
        Area roleArea = parseArea(areaElement);
        // Get the line text
        Element lineElement = (Element) partElement.getElementsByTagName("line").item(0);
        String lineText = lineElement.getTextContent().trim();
        // Create Role object
        return new Role(roleName, roleLevel, roleArea, lineText, isCard);
    }

}
