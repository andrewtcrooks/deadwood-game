import java.util.*;

/**
 * Represents a shuffled deck of SceneCard objects.
 */
public class Deck {
    // create a list for undrawn cards
    private List<SceneCard> undrawnCards;
    // create an empty list for drawn cards
    private List<SceneCard> drawnCards = new ArrayList<>();


    /**
     * Initializes a new shuffled Deck with SceneCard objects created from the data in the XML file at the given path.
     *
     * @param cards The list of SceneCard objects to populate the deck with.
     */
    public Deck(List<SceneCard> cards) {
        // populate the deck of undrawn cards
        this.undrawnCards = new ArrayList<>(cards);
        // shuffle the deck of undrawn cards
        this.shuffle();
        // populate the list of drawn cards
        this.drawnCards = new ArrayList<>();
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
     * @param sceneCardID the ID of the SceneCard to discard
     */
    public void discardCard(int sceneCardID) {
        SceneCard card = getDrawnCard(sceneCardID);
        // remove the card from the drawn deck
        this.drawnCards.remove(card);
    }

    /**
     * Returns the specific drawn SceneCard with the given ID
     * @param sceneCardID the ID of the SceneCard to return
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
    }
}
