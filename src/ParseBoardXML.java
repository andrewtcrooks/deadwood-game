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
    private Map<String, Location> locations;

    /**
     * Initializes a new ParseBoardXML object.
     */
    public ParseBoardXML() {
        this.locations = new HashMap<String, Location>();
    }

    /**
     * Reads the data from the given Document object.
     * 
     * @param d The Document object to read data from.
     * @return void
     * @throws Exception
     */
    @Override
    public void readData(Document d) throws Exception {
        Element root = d.getDocumentElement();
        NodeList locationsList = root.getElementsByTagName("set");
        parseLocations(locationsList);
    }

    /**
     * Parses the locations from the given NodeList object.
     * 
     * @param locationsList
     */
    private void parseLocations(NodeList locationsList) {
        for (int i = 0; i < locationsList.getLength(); i++) {
            Node location = locationsList.item(i);
            Location newLocation = parseLocation((Element) location);
            locations.put(newLocation.getName(), newLocation);
        }
    }

    /**
     * Parses a location from the given Element object.
     * 
     * @param setElement
     * @return Location
     */
    private Location parseLocation(Element setElement) {
        
        // Parse name
        String name = parseName(setElement);

        // Parse neighbors
        List<String> neighbors = parseNeighbors(setElement);
    
        // Parse area
        Element areaElement = (Element) setElement.getElementsByTagName("area").item(0);
        Area area = parseArea(areaElement);

        // Parse takes
        List<Take> takes = parseTakes(setElement);
    
        // Parse parts
        List<Role> roles = parseRoles(setElement);
    
        return new Location(name, neighbors, area, takes,  roles);
    }

    /**
     * Parses the name from the given Element object.
     * 
     * @param setElement
     * @return String
     */
    private String parseName(Element setElement) {
        return setElement.getAttribute("name");
    }

    /**
     * Parses the neighbors from the given Element object.
     * 
     * @param setElement
     * @return List<String>
     */
    private List<String> parseNeighbors(Element setElement) {
        List<String> neighbors = new ArrayList<>();
        NodeList neighborsList = setElement.getElementsByTagName("neighbor");
        for (int i = 0; i < neighborsList.getLength(); i++) {
            Element neighborElement = (Element) neighborsList.item(i);
            neighbors.add(neighborElement.getAttribute("name"));
        }
        return neighbors;
    }
    
    /**
     * Parses the takes from the given Element object.
     * 
     * @param setElement
     * @return int
     */
    private List<Take> parseTakes(Element setElement) {
        NodeList takesList = setElement.getElementsByTagName("take");
        List<Take> takes = new ArrayList<>();
        for (int i = 0; i < takesList.getLength(); i++) {
            Node takeNode = takesList.item(i);
            Take take = parseTake((Element) takeNode);
            takes.add(take);
        }
        return takes;
    }
    
    /*
     * Parses a take from the given Element object.
     * 
     * @param takeElement
     * @return Take
     */
    Take parseTake(Element takeElement) {
        int number = Integer.parseInt(takeElement.getAttribute("number"));
        Element areaElement = (Element) takeElement.getElementsByTagName("area").item(0);
        Area area = parseArea(areaElement);
        return new Take(number, area);
    }

    /**
     * Parses the roles from the given Element object.
     * 
     * @param setElement
     * @return List<Role>
     */
    private List<Role> parseRoles(Element setElement) {
        List<Role> roles = new ArrayList<>();
        NodeList partsList = setElement.getElementsByTagName("part");
        for (int j = 0; j < partsList.getLength(); j++) {
            Node partNode = partsList.item(j);
            Role role = parseRole((Element) partNode);
            roles.add(role);
        }
        return roles;
    }
    
    /**
     * Parses a role from the given Element object.
     * 
     * @param partElement
     * @return Role
     */
    private Role parseRole(Element partElement) {
        String roleName = partElement.getAttribute("name");
        int roleLevel = Integer.parseInt(partElement.getAttribute("level"));
    
        // Parse the Area
        Element areaElement = (Element) partElement.getElementsByTagName("area").item(0);
        Area roleArea = parseArea(areaElement);

        // Get the line text
        Element lineElement = (Element) partElement.getElementsByTagName("line").item(0);
        String lineText = lineElement.getTextContent().trim();
    
        // Create Role object
        return new Role(roleName, roleLevel, roleArea, lineText, false);
    }

    /**
     * Parses an area from the given Element object.
     * 
     * @param areaElement
     * @return Area
     */
    private Area parseArea(Element areaElement) {
        int x = Integer.parseInt(areaElement.getAttribute("x"));
        int y = Integer.parseInt(areaElement.getAttribute("y"));
        int h = Integer.parseInt(areaElement.getAttribute("h"));
        int w = Integer.parseInt(areaElement.getAttribute("w"));
        return new Area(x, y, h, w);
    }

    /**
     * Returns the parsed locations.
     * 
     * @return Map<String, Location>
     */
    public Map<String, Location> getLocations() {
        return locations;
    }
}