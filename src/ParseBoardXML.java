import org.w3c.dom.*;
import java.util.*;

/**
 * Represents a class for parsing board data from an XML file.
 * This class extends the AbstractParseXML class.
 * 
 * This class provides a method for reading data from a Document object.
 * This class also provides a method for getting the parsed locations.
 */
public class ParseBoardXML extends AbstractParseXML {
    private List<Location> locations;

    /**
     * Initializes a new ParseBoardXML object.
     */
    public ParseBoardXML() {
        locations = new ArrayList<Location>();
    }

    /**
     * Reads the data from the given Document object.
     * 
     * @param d The Document object to read data from.
     * @return void
     * @throws Exception
     */
    @Override
    public void readData(Document d) {
        NodeList setsList = d.getElementsByTagName("set");
        for (int i = 0; i < setsList.getLength(); i++) {
            Node set = setsList.item(i);
            if (set.getNodeType() == Node.ELEMENT_NODE) {
                Element setElement = (Element) set;
                String name = setElement.getAttribute("name");
    
                // Parse neighbors
                List<String> neighbors = new ArrayList<>();
                NodeList neighborsList = ((Element) setElement.getElementsByTagName("neighbors").item(0)).getElementsByTagName("neighbor");
                for (int j = 0; j < neighborsList.getLength(); j++) {
                    Node neighborNode = neighborsList.item(j);
                    if (neighborNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element neighborElement = (Element) neighborNode;
                        String neighborName = neighborElement.getAttribute("name");
                        neighbors.add(neighborName);
                    }
                }
    
                // Parse takes
                int shots = 0;
                NodeList takesList = ((Element) setElement.getElementsByTagName("takes").item(0)).getElementsByTagName("take");
                if (takesList.getLength() > 0) {
                    Node firstTakeNode = takesList.item(0);
                    if (firstTakeNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element firstTakeElement = (Element) firstTakeNode;
                        shots = Integer.parseInt(firstTakeElement.getAttribute("number"));
                    }
                }
    
                // Parse roles
                List<Role> roles = new ArrayList<>();
                NodeList partsList = ((Element) setElement.getElementsByTagName("parts").item(0)).getElementsByTagName("part");
                for (int j = 0; j < partsList.getLength(); j++) {
                    Node partNode = partsList.item(j);
                    if (partNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element partElement = (Element) partNode;
                        String roleName = partElement.getAttribute("name");
                        int roleLevel = Integer.parseInt(partElement.getAttribute("level"));

                        // Get the line text
                        Element lineElement = (Element) partElement.getElementsByTagName("line").item(0);
                        String lineText = lineElement.getTextContent().trim();

                        // Create Role object
                        Role role = new Role(roleName, roleLevel, lineText, true);
                        roles.add(role);
                    }
                }

                // Create a new Location object with the parsed attributes
                Location newLocation = new Location(name, shots, neighbors, roles);
    
                // Add the new Location object to the locations list
                locations.add(newLocation);
            }
        }
    }

    /**
     * Returns the parsed locations.
     * 
     * @return
     */
    public List<Location> getLocations() {
        return locations;
    }
}