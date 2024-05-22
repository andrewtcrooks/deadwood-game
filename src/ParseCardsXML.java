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
        for (int i = 0; i < cardsList.getLength(); i++) {
            Node card = cardsList.item(i);
            SceneCard newCard = parseCard((Element) card);
            cards.add(newCard);
        }
    }
    
    private SceneCard parseCard(Element cardElement) {
        String title = cardElement.getAttribute("name");
        String image = cardElement.getAttribute("img");
        int budget = Integer.parseInt(cardElement.getAttribute("budget"));
    
        // Parse scene
        Element sceneElement = (Element) cardElement.getElementsByTagName("scene").item(0);
        int id = Integer.parseInt(sceneElement.getAttribute("number"));
        String desc = sceneElement.getTextContent().trim();
    
        // Parse roles
        List<Role> roles = parseRoles(cardElement);
    
        // Create a new SceneCard object with the parsed attributes
        return new SceneCard(id, title, desc, image, budget, roles);
    }
    
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
    
    private Role parseRole(Element partElement) {
        String roleName = partElement.getAttribute("name");
        int roleLevel = Integer.parseInt(partElement.getAttribute("level"));
    
        // Get the line text
        Element lineElement = (Element) partElement.getElementsByTagName("line").item(0);
        String lineText = lineElement.getTextContent().trim();
    
        // Create Role object
        return new Role(roleName, roleLevel, lineText, true);
    }

    /**
     * Returns the parsed scene cards.
     * 
     * @return
     */
    public List<SceneCard> getCards() {
        return cards;
    }
}
