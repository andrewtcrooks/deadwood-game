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
        super();
    }

    /**
     * Returns a NodeList object of the Element objects from the given filename.
     * This includes both <set> elements and other top-level location elements like <trailer> and <office>.
     * 
     * @param fileName The XML file name to read from.
     * @return the NodeList object of the Element objects
     * @throws Exception if parsing fails.
     */
    @Override
    public NodeList readData(String fileName) throws Exception {
        Document d = getDocFromFile(fileName);
        Element root = d.getDocumentElement();

        // Collect all <set>, <trailer>, and <office> elements
        List<Node> allLocations = new ArrayList<>();

        // Add all <set> elements
        NodeList setList = root.getElementsByTagName("set");
        for (int i = 0; i < setList.getLength(); i++) {
            allLocations.add(setList.item(i));
        }

        // Add <trailer> element if it exists
        Node trailer = root.getElementsByTagName("trailer").item(0);
        if (trailer != null) {
            allLocations.add(trailer);
        }

        // Add <office> element if it exists
        Node office = root.getElementsByTagName("office").item(0);
        if (office != null) {
            allLocations.add(office);
        }

        return new NodeListWrapper(allLocations);
    }

    /**
     * Returns a map of the parsed locations from the given NodeList object.
     * 
     * @param locationsList NodeList containing location elements.
     * @return the map of parsed locations
     */
    public Map<String, Location> getLocations(String fileName) {
        Map<String, Location> locations = new HashMap<>();
        try {
            NodeList locationsNodeList = readData(fileName);
            locations = parseLocations(locationsNodeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations;
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
            if (location.getNodeType() == Node.ELEMENT_NODE) {
                Location newLocation = parseLocation((Element) location);
                locations.put(newLocation.getName(), newLocation);
            }
        }
        return locations;
    }

    /**
     * Parses a location from the given Element object.
     * 
     * @param locationElement The XML Element representing the location.
     * @return the parsed location
     */
    private Location parseLocation(Element locationElement) {
        String tagName = locationElement.getTagName();
        String name;

        if ("set".equalsIgnoreCase(tagName)) {
            name = parseName(locationElement);
        } else if ("office".equalsIgnoreCase(tagName)) {
            // Explicitly map <office> to "Casting Office"
            name = "Casting Office";
        } else {
            // For <trailer>, use the tag name with proper casing
            name = capitalizeFirstLetter(tagName);
        }

        // Parse neighbors
        List<String> neighbors = parseNeighbors(locationElement);

        // Parse area
        Element areaElement = (Element) locationElement.getElementsByTagName("area").item(0);
        Area area = parseArea(areaElement);

        // Parse takes
        List<Take> takes = parseTakes(locationElement);

        // Parse parts (only for <set> elements)
        List<Role> roles = new ArrayList<>();
        if ("set".equalsIgnoreCase(tagName)) {
            roles = parseRoles(locationElement, false);
        }

        // Create a new Location object with the parsed attributes
        return new Location(name, neighbors, area, takes, roles);
    }

    /**
     * Parses the neighbors from the given Element object.
     * 
     * @param locationElement The XML Element representing the location.
     * @return the list of parsed neighbors as strings
     */
    private List<String> parseNeighbors(Element locationElement) {
        List<String> neighbors = new ArrayList<>();
        NodeList neighborsList = locationElement.getElementsByTagName("neighbor");
        for (int i = 0; i < neighborsList.getLength(); i++) {
            Element neighborElement = (Element) neighborsList.item(i);
            String neighborName = neighborElement.getAttribute("name");
            // Normalize neighbor names
            neighborName = normalizeNeighborName(neighborName);
            neighbors.add(neighborName);
        }
        return neighbors;
    }

    /**
     * Normalizes neighbor names for consistency.
     *
     * @param neighborName The original neighbor name.
     * @return Normalized neighbor name.
     */
    private String normalizeNeighborName(String neighborName) {
        if ("office".equalsIgnoreCase(neighborName)) {
            return "Casting Office";
        } else if ("trailer".equalsIgnoreCase(neighborName)) {
            return "Trailer";
        }
        return neighborName;
    }

    /**
     * Parses the takes from the given Element object.
     * 
     * @param locationElement The XML Element representing the location.
     * @return the list of parsed takes
     */
    private List<Take> parseTakes(Element locationElement) {
        NodeList takesList = locationElement.getElementsByTagName("take");
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
     * @param takeElement The XML Element representing the take.
     * @return the parsed take
     */
    private Take parseTake(Element takeElement) {
        int number = Integer.parseInt(takeElement.getAttribute("number"));
        Element areaElement = (Element) takeElement.getElementsByTagName("area").item(0);
        Area area = parseArea(areaElement);
        return new Take(number, area);
    }

    /**
     * Capitalizes the first letter of the given string.
     *
     * @param str The input string.
     * @return The string with the first letter capitalized.
     */
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * A simple wrapper to convert a List<Node> to a NodeList.
     */
    private class NodeListWrapper implements NodeList {
        private final List<Node> nodes;

        public NodeListWrapper(List<Node> nodes) {
            this.nodes = nodes;
        }

        @Override
        public Node item(int index) {
            return nodes.get(index);
        }

        @Override
        public int getLength() {
            return nodes.size();
        }
    }
}
