import org.w3c.dom.*;
import java.util.*;

/**
 * Represents a class for parsing board data from an XML file.
 * This class extends the AbstractParseXML class.
 * <p>
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
     * Returns a NodeList object of the Element objects from the given filename.
     * 
     * @param d The Document object to read data from.
     * @return the NodeList object of the Element objects
     * @throws Exception
     */
    @Override
    public NodeList readData(String fileName) throws Exception {
        Document d = getDocFromFile(fileName);
        Element root = d.getDocumentElement();
        NodeList locationsNodeList = root.getElementsByTagName("set");
        return locationsNodeList;
    }

    /**
     * Returns a map of the parsed locations from the given NodeList object.
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
     * @return the parsed location
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
     * @return the list of parsed neighbors as strings
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
     * @return the list of parsed takes
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
     * @return the parsed take
     */
    private Take parseTake(Element takeElement) {
        int number = Integer.parseInt(takeElement.getAttribute("number"));
        Element areaElement = (Element) takeElement.getElementsByTagName("area").item(0);
        Area area = parseArea(areaElement);
        return new Take(number, area);
    }

    /**
     * Returns a map of the parsed locations from the given file.
     * 
     * @param filename
     * @return the map of parsed locations
     */
    public Map<String, Location> getLocations(String fileName) {
        Map<String, Location> locations = null;
        try {
            NodeList locationsNodeList = readData(fileName);
            locations = parseLocations(locationsNodeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations;
    }

}