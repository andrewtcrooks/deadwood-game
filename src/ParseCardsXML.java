import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.*;
import java.util.*;


public class ParseCardsXML implements IParseXML {
    private ArrayList<SceneCard> cards;

    public ParseCardsXML() {
        cards = new ArrayList<SceneCard>();
    }

    public Document getDocFromFile(String filename) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(filename);
        return doc;
    }

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

    public ArrayList<SceneCard> getCards() {
        return cards;
    }
}
