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


    /**
     * Initializes a new ParseBoardXML object.
     */
    public ParseBoardXML() {
    }

    /**
     * Reads the data from the given Document object.
     * 
     * @param d The Document object to read data from.
     * @return Element
     * @throws Exception
     */
    @Override
    public Element readData(Document d) throws Exception {
        Element root = d.getDocumentElement();
        return root;
    }

    /**
     * Parses the locations from the given NodeList object.
     * 
     * @param locationsList
     * @return the map of parsed locations
     */
    private Map<String, Location> parseLocations(NodeList locationsList) {
        Map<String, Location> locations = new HashMap<>();
        for (int i = 0; i < locationsList.getLength(); i++) {
            Node location = locationsList.item(i);
            Location newLocation = parseLocation((Element) location);
            locations.put(newLocation.getName(), newLocation);
        }
        return locations;
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
        List<Role> roles = parseRoles(setElement, false);
        // Create a new Location object with the parsed attributes
        return new Location(name, neighbors, area, takes,  roles);
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
    
    /**
     * Parses a take from the given Element object.
     * 
     * @param takeElement
     * @return Take
     */
    private Take parseTake(Element takeElement) {
        int number = Integer.parseInt(takeElement.getAttribute("number"));
        Element areaElement = (Element) takeElement.getElementsByTagName("area").item(0);
        Area area = parseArea(areaElement);
        return new Take(number, area);
    }

    /**
     * Returns the parsed locations.
     * 
     * @return Map<String, Location>
     */
    public Map<String, Location> getLocations(String filename) {
        Map<String, Location> locations = null;
        try {
            Document d = getDocFromFile(filename);
            Element root = readData(d);
            NodeList locationsList = root.getElementsByTagName("set");
            locations = parseLocations(locationsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations;
    }

}