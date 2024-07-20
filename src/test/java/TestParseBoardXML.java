import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.w3c.dom.Document;

public class TestParseBoardXML {

    @Test
    void testGetDocFromFile() {
        ParseBoardXML parser = new ParseBoardXML();
        Document doc = null;
        try {
            doc = parser.getDocFromFile("resources/Board.xml");
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
        assertNotNull(doc, "Document should not be null");
    }
    @Test
    void testReadData() {
        ParseBoardXML parser = new ParseBoardXML();
        Document doc = null;
        try {
            doc = parser.getDocFromFile("resources/Board.xml");
            parser.readData(doc);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    void testGetLocations() {
        ParseBoardXML parser = new ParseBoardXML();
        Document doc = null;
        try {
            doc = parser.getDocFromFile("resources/Board.xml");
            parser.readData(doc);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
        Map<String, Location> locations = parser.getLocations();
        assertEquals(10, locations.size(), "There should be 10 locations");
    }

    @Test
    void testLocationProperties() {
        ParseBoardXML parser = new ParseBoardXML();
        Document doc = null;
        try {
            doc = parser.getDocFromFile("resources/Board.xml");
            parser.readData(doc);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
        Map<String, Location> locations = parser.getLocations();
        Location location = locations.get("Train Station"); // Test the first location

        // Check the name of the location
        assertEquals("Train Station", location.getName(), "Location name should be 'Train Station'");

        // Check the number of shots at the location
        assertEquals(3, location.getShots(), "Location should have 3 shots");

        // Check the neighbors of the location
        List<String> neighbors = location.getNeighbors();
        assertTrue(neighbors.contains("Jail"), "Train Station should have Jail as a neighbor");
        assertTrue(neighbors.contains("General Store"), "Train Station should have General Store as a neighbor");
        assertTrue(neighbors.contains("office"), "office should have Train Station as a neighbor");

        // Check the area of the location
        Area area = location.getArea();
        assertEquals(21, area.getX());
        assertEquals(69, area.getY());
        assertEquals(115, area.getH());
        assertEquals(205, area.getW());
        
        // Check the takes of the location
        List<Take> takes = location.getTakes();
        assertEquals(3, takes.size(), "Location should have 3 take");

        // Check the roles of the location
        List<Role> roles = location.getRoles();
        assertEquals(4, roles.size(), "Location should have 4 roles");
        
        // Check the name of the first role
        Role role = roles.get(0);
        assertEquals("Crusty Prospector", role.getName(), "First role should be 'Crusty Prospector'");

        // Check the name of the second role
        role = roles.get(1);
        assertEquals("Dragged by Train", role.getName(), "Second role should be 'Dragged by Train'");

        // Check the name of the third role
        role = roles.get(2);
        assertEquals("Preacher with Bag", role.getName(), "Third role should be 'Preacher with Bag'");

        // Check the name of the fourth role
        role = roles.get(3);
        assertEquals("Cyrus the Gunfighter", role.getName(), "Fourth role should be 'Cyrus the Gunfighter'");        
    }
}
