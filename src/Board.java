import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Represents the game board for the Deadwood game.
 */
public class Board {
    private int numDays;
    private int numScenesRemaining;
    private List<Player> players;
    private Map<String, Location> locations;
    private Deck deck;

    /**
     * Constructs a new Board with the specified number of days and players.
     *
     * @param numDays the number of days for the game
     * @param players the list of players in the game
     */
    Board(int numDays, List<Player> players, String xmlFilePath) {
        this.numDays = numDays;
        this.numScenesRemaining = 10;
        this.players = players;
        this.deck = new Deck("resources/cards.xml");
        this.locations = new HashMap<String, Location>();
        initLocations(xmlFilePath);
    }

    /**
     * Initializes the locations on the board.
     */
    private void initLocations(String xmlFilePath) {
        ParseBoardXML parser = new ParseBoardXML();
        try {
            Document doc = parser.getDocFromFile(xmlFilePath);
            parser.readData(doc);
            this.locations = parser.getLocations();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the number of days remaining in the game.
     *
     * @return The number of days remaining in the game.
     */
    int getNumDays() {
        return this.numDays;
    }

    /**
     * Decrements the number of days remaining in the game by 1.
     *
     * @return The new number of days remaining in the game.
     */
    int decrementNumDays() {
        return --this.numDays;
    } 

    /**
     * Returns the number of scenes remaining in the game.
     *
     * @return The number of scenes remaining in the game.
     */
    int getNumScenesRemaining() {
        return this.numScenesRemaining;
    }

    /**
     * Decrements the number of scenes remaining in the game by 1.
     *
     * @return The new number of scenes remaining in the game.
     */
    int decrementNumScenesRemaining() {
        return --this.numScenesRemaining;
    }

    /**
     * Returns the number of shots remaining at a location.
     *  
     * @param location The location to check.
     * @return The number of shots remaining at the location.
     */
    int getLocationShotsRemaining(Location location) {
        // TODO: Implement logic to return shots remaining at a location
        return 0;
    }

    /**
     * Decrements the number of shots remaining at a location by 1.
     *  
     * @param location The location to decrement shots at.
     */
    void setLocationShotsDecrement(Location location) {
        // TODO: Implement logic to decrement shots remaining at location by 1
    }

    /**
     * Resets the number of shots remaining at a location to the initial value.
     *  
     * @param location The location to reset shots at.
     */
    void setLocationShotsReset(Location location) {
        // TODO: Implement logic to reset shots remaining at location to initial value
    }

    /**
     * Returns the neighbors of a location.
     *
     * @param location The location to check.
     * @return The neighbors of the location.
     */
    List<Location> getLocationNeighbors(Location location) {
        // TODO: Implement logic to return neighbors of a location as list of Location objects using locations Map
        return null;
    }

    /**
     * Returns the roles at a location.
     *
     * @param location The location to check.
     * @return The roles at the location.
     */
    List<Role> getLocationRoles(Location location) {
        return location.getRoles();
    }

    /**
     * Returns the scene card at a location.
     *
     * @param location The location to check.
     * @return The scene card at the location.
     */
    SceneCard getLocationScene(Location location) {
        return location.getScene();
    }

    /** Deal new Scene Cards to all lcoations
     * 
     * @param locations
     */ 
    void dealNewSceneCards(List<Location> locations) {
        for (Location location : locations) {
            location.setScene(deck.drawCard());
        }
    }

    /**
     * Resets the board to its initial state.
     */
    void resetBoard() {
        resetPlayerLocations();
        resetNumScenesRemaining();
        dealNewSceneCards(new ArrayList<>(locations.values()));
    }

    /**
     * Change all player locations on the board to "Trailer"
     */
    void resetPlayerLocations() {
        for (Player player : players) {
            player.setLocation(locations.get("trailer"));
        }
    }

    /**
     * Resets the number of scenes remaining in the game to 10.
     */
    void resetNumScenesRemaining() {
        this.numScenesRemaining = 10;
    }

}