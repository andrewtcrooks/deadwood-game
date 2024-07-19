import java.util.*;

/**
 * Represents a shuffled deck of SceneCard objects.
 */
public class Deck {
    private List<SceneCard> undrawnCards;
    // create an empty list for drawn cards
    private List<SceneCard> drawnCards = new ArrayList<>();
    // create an empty list for discarded cards
    private List<SceneCard> discardedCards = new ArrayList<>();


    /**
     * Initializes a new shuffled Deck with SceneCard objects created from the data in the XML file at the given path.
     *
     * @param xmlFilePath The path to the XML file containing the card data.
     */
    public Deck(String cardsXMLFilePath) {
        ParseCardsXML parser = new ParseCardsXML();
        try {
            // Get the cards from the parser
            this.undrawnCards = parser.getCards(cardsXMLFilePath);
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
        if (!this.undrawnCards.isEmpty()) {
            // remove the top card from the undrawn deck
            SceneCard card = this.undrawnCards.remove(0);
            // add the card to the drawn deck
            this.drawnCards.add(card);
            // return the card
            return card;
        } else {
            throw new IllegalStateException("Deck is empty");
        }
    }

    /**
     * Shuffles the cards in the deck.
     */
    public void shuffle() {
        Collections.shuffle(this.undrawnCards);
    }
    
    /**
     * Returns if the deck is empty.
     * @return true if the deck is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.undrawnCards.isEmpty();
    }
    
    /**
     * Discards the given SceneCard.
     * @param card the SceneCard to discard
     */
    public void discardCard(int sceneCardID) {
        SceneCard card = getDrawnCard(sceneCardID);
        // remove the card from the drawn deck
        this.drawnCards.remove(card);
        // add the card to the discarded deck
        this.discardedCards.add(card);
    }

    /**
     * Returns the specific drawn SceneCard with the given ID
     * @param sceneCardID
     * @return the SceneCard with the given ID
     */
    public SceneCard getDrawnCard(int sceneCardID) {
        for (SceneCard card : this.drawnCards) {
            int id = card.getID();
            if (id == sceneCardID) {
                return card;
            }
        }
        // Return null if no card matches the given ID
        return null; 
    }

    /**
     * Removes the last card from the drawn cards and adds it to the discarded cards.
     */
    public void discardLastDrawnCard() {
        SceneCard lastCard = this.drawnCards.get(0);
        this.drawnCards.remove(lastCard);
        this.discardedCards.add(lastCard);
    }
}
