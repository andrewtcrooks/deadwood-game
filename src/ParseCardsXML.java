import org.w3c.dom.*;
import java.util.*;

/**
 * Represents a class for parsing scene cards from an XML file.
 * This class extends the AbstractParseXML class.
 * 
 * This class provides a method for reading data from a Document object.
 * This class also provides a method for getting the parsed scene cards.
 */
public class ParseCardsXML extends AbstractParseXML {
    private List<SceneCard> cards;

    /**
     * Initializes a new ParseCardsXML object.
     */
    public ParseCardsXML() {
        this.cards = new ArrayList<>();
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
        NodeList cardsList = root.getElementsByTagName("card");
        parseCards(cardsList);
    }
    
    /**
     * Parses the scene cards from the given NodeList object.
     * 
     * @param cardsList
     */
    private void parseCards(NodeList cardsList) {
        for (int i = 0; i < cardsList.getLength(); i++) {
            Node card = cardsList.item(i);
            SceneCard newCard = parseCard((Element) card);
            cards.add(newCard);
        }
    }

    /**
     * Parses a scene card from the given Element object.
     * 
     * @param cardElement
     * @return SceneCard
     */
    private SceneCard parseCard(Element cardElement) {

        // Parse name
        String title = parseName(cardElement);

        // Parse image
        String image = parseImage(cardElement);

        // Parse budget
        int budget = parseBudget(cardElement);
    
        // Parse scene ID
        int id = parseSceneID(cardElement);

        // Parse scene description
        String desc = parseSceneDescription(cardElement);
    
        // Parse roles
        List<Role> roles = parseRoles(cardElement);
    
        // Create a new SceneCard object with the parsed attributes
        return new SceneCard(id, title, desc, image, budget, roles);
    }
    
    /**
     * Parses the name from the given Element object.
     * 
     * @param cardElement
     * @return String
     */
    private String parseName(Element cardElement) {
        return cardElement.getAttribute("name");
    }

    /**
     * Parses the image from the given Element object.
     * 
     * @param cardElement
     * @return String
     */
    private String parseImage(Element cardElement) {
        return cardElement.getAttribute("img");
    }

    /**
     * Parses the budget from the given Element object.
     * 
     * @param cardElement
     * @return int
     */
    private int parseBudget(Element cardElement) {
        return Integer.parseInt(cardElement.getAttribute("budget"));
    }

    /**
     * Parses the scene ID from the given Element object.
     * 
     * @param cardElement
     * @return int
     */
    private int parseSceneID(Element cardElement) {
        Element sceneElement = (Element) cardElement.getElementsByTagName("scene").item(0);
        int id = Integer.parseInt(sceneElement.getAttribute("number"));
        return id;
    }

    /**
     * Parses the scene description from the given Element object.
     * 
     * @param cardElement
     * @return String
     */
    private String parseSceneDescription(Element cardElement) {
        Element sceneElement = (Element) cardElement.getElementsByTagName("scene").item(0);
        String desc = sceneElement.getTextContent().trim();
        return desc;
    }

    /**
     * Parses the roles from the given Element object.
     * 
     * @param cardElement
     * @return List<Role>
     */
    private List<Role> parseRoles(Element cardElement) {
        List<Role> roles = new ArrayList<>();
        NodeList partsList = cardElement.getElementsByTagName("part");
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
        return new Role(roleName, roleLevel, roleArea, lineText, true);
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
     * Returns the parsed scene cards.
     * 
     * @return List<SceneCard>
     */
    public List<SceneCard> getCards() {
        return cards;
    }
}
