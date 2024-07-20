import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class TestDeck {

    @Test
    void testDeckInitialization() {
        Deck deck = new Deck("resources/cards.xml");
        assertFalse(deck.isEmpty(), "Deck should not be empty after initialization");
    }

    @Test
    void testDeckDrawCard() {
        Deck deck = new Deck("resources/cards.xml");
        SceneCard card = deck.drawCard();
        assertNotNull(card, "Drawn card should not be null");
    }

    @Test
    void testDeckIsEmpty() {
        Deck deck = new Deck("resources/cards.xml");
        while (!deck.isEmpty()) {
            deck.drawCard();
        }
        assertTrue(deck.isEmpty(), "Deck should be empty after all cards are drawn");
    }

    @Test
    void testDeckShuffle() {
        Deck deck1 = new Deck("resources/cards.xml");
        Deck deck2 = new Deck("resources/cards.xml");
        deck1.shuffle();
        assertNotEquals(deck1.drawCard().getTitle(), deck2.drawCard().getTitle(), "After shuffling, the first card of two decks should not be the same");
    }

    @Test
    void testDeckDrawCardWhenEmpty() {
        Deck deck = new Deck("resources/cards.xml");
        while (!deck.isEmpty()) {
            deck.drawCard();
        }
        assertThrows(IllegalStateException.class, deck::drawCard, "Should throw IllegalStateException when trying to draw card from an empty deck");
    }
}




