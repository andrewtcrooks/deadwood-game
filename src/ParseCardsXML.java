import org.w3c.dom.*;
import java.util.*;

/**
 * Represents a class for parsing scene cards from an XML file.
 * This class implements the IParseXML interface.
 * 
 * This class provides a method for reading data from a Document object.
 * This class also provides a method for getting the parsed scene cards.
 */
public class ParseCardsXML extends AbstractParseXML  {
    private ArrayList<SceneCard> cards;

    /**
     * Initializes a new ParseCardsXML object.
     */
    public ParseCardsXML() {
        cards = new ArrayList<SceneCard>();
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
        NodeList cardsList = d.getElementsByTagName("card");
        for (int i = 0; i < cardsList.getLength(); i++) {
            Node card = cardsList.item(i);
            if (card.getNodeType() == Node.ELEMENT_NODE) {
                Element cardElement = (Element) card;
                String title = cardElement.getAttribute("name");
                String image = cardElement.getAttribute("img");
                int budget = Integer.parseInt(cardElement.getAttribute("budget"));

                // Parse scene
                Element sceneElement = (Element) cardElement.getElementsByTagName("scene").item(0);
                int id = Integer.parseInt(sceneElement.getAttribute("number"));
                String desc = sceneElement.getTextContent().trim();

                // Parse roles
                List<Role> roles = new ArrayList<>();
                NodeList partsList = cardElement.getElementsByTagName("part");
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

                // Create a new SceneCard object with the parsed attributes
                SceneCard newCard = new SceneCard(id, title, desc, image, budget, roles);

                // Add the new SceneCard object to the cards list
                cards.add(newCard);
            }
        }
    }

    /**
     * Returns the parsed scene cards.
     * 
     * @return
     */
    public ArrayList<SceneCard> getCards() {
        return cards;
    }
}
