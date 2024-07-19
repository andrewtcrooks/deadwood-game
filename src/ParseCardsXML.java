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
        List<Role> roles = parseRoles(cardElement, true);
        // Create a new SceneCard object with the parsed attributes
        return new SceneCard(title, image, budget, id, desc, roles);
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
     * Returns the parsed scene cards.
     * 
     * @return List<SceneCard>
     */
    public List<SceneCard> getCards() {
        return cards;
    }

}
