import java.util.*;
import org.w3c.dom.Document;

/**
 * Represents a shuffled deck of SceneCard objects.
 */
public class Deck {
    private List<SceneCard> cards;


    /**
     * Initializes a new shuffled Deck with SceneCard objects created from the data in the XML file at the given path.
     *
     * @param xmlFilePath The path to the XML file containing the card data.
     */
    public Deck(String xmlFilePath) {
        ParseCardsXML parser = new ParseCardsXML();
        try {
            Document doc = parser.getDocFromFile(xmlFilePath);
            // Parse the data from the XML file
            parser.readData(doc);
            // Get the cards from the parser
            this.cards = parser.getCards();
            // Shuffle the deck
            this.shuffle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes and returns the top card from the deck.
     *
     * @return The top card from the deck.
     * @throws IllegalStateException If the deck is empty.
     */
    public SceneCard drawCard() {
        if (!this.cards.isEmpty()) {
            // Remove and return the top card
            return this.cards.remove(0);
        } else {
            throw new IllegalStateException("Deck is empty");
        }
    }

    /**
     * Shuffles the cards in the deck.
     */
    public void shuffle() {
        Collections.shuffle(this.cards);
    }
    
    /**
     * Returns if the deck is empty.
     * @return true if the deck is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    /**
     * Returns the specific SceneCard with the given ID
     * @param sceneCardID
     * @return the SceneCard with the given ID
     */
    public SceneCard getSceneCard(int sceneCardID) {
        return this.cards.get(sceneCardID);
    }
    
}
