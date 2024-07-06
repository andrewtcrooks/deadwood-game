import java.util.*;
import org.w3c.dom.Document;


/**
 * Represents a deck of SceneCard objects.
 */
public class Deck {
    private List<SceneCard> cards;

    /**
     * Initializes a new Deck with SceneCard objects created from the data in the XML file at the given path.
     * The cards are then shuffled.
     *
     * @param xmlFilePath The path to the XML file containing the card data.
     */
    Deck(String xmlFilePath) {
        ParseCardsXML parser = new ParseCardsXML();
        try {
            Document doc = parser.getDocFromFile(xmlFilePath);
            parser.readData(doc);
            this.cards = parser.getCards();
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
    SceneCard drawCard() {
        if (!this.cards.isEmpty()) {
            return this.cards.remove(this.cards.size() - 1);
        } else {
            throw new IllegalStateException("Deck is empty");
        }
    }

    /**
     * Shuffles the cards in the deck.
     */
    void shuffle() {
        Collections.shuffle(this.cards);
    }
    
    /**
     * Returns true if the deck is empty, false otherwise.
     */
    boolean isEmpty() {
        return this.cards.isEmpty();
    }
}
