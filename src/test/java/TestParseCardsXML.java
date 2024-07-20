import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.w3c.dom.Document;


public class TestParseCardsXML {

    @Test
    void testGetDocFromFile() {
        ParseCardsXML parser = new ParseCardsXML();
        Document doc = null;
        try {
            doc = parser.getDocFromFile("resources/cards.xml");
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
        assertNotNull(doc, "Document should not be null");
    }

    @Test
    void testReadData() {
        ParseCardsXML parser = new ParseCardsXML();
        Document doc = null;
        try {
            doc = parser.getDocFromFile("resources/cards.xml");
            parser.readData(doc);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    void testGetCards() {
        ParseCardsXML parser = new ParseCardsXML();
        Document doc = null;
        try {
            doc = parser.getDocFromFile("resources/cards.xml");
            parser.readData(doc);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
        List<SceneCard> cards = parser.getCards();
        assertEquals(40, cards.size(), "There should be 40 cards");
    }

    @Test
    void testCardProperties() {
        ParseCardsXML parser = new ParseCardsXML();
        Document doc = null;
        try {
            doc = parser.getDocFromFile("resources/cards.xml");
            parser.readData(doc);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
        List<SceneCard> cards = parser.getCards();
        SceneCard card = cards.get(0); // Test the first card
    
        // Check the title of the card
        assertEquals("Evil Wears a Hat", card.getTitle(), "Card title should be 'Evil Wears a Hat'");
    
        // Check the budget of the card
        assertEquals(4, card.getBudget(), "Card budget should be 4");
    
        // Check the roles of the card
        List<Role> roles = card.getRoles();
        assertEquals(3, roles.size(), "Card should have 3 roles");
    
        // Check the name of the first role
        assertEquals("Defrocked Priest", roles.get(0).getName(), "First role name should be 'Defrocked Priest'");
    }

}
