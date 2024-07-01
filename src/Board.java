import java.util.*;
import org.w3c.dom.Document;

/**
 * Represents the game board for the Deadwood game.
 */
public class Board {
    private int numDays;
    private int numScenesRemaining;
    private List<Player> players;
    private Deck deck;
    private Map<String, Location> locations;
    private String boardXMLFilePath;


    /**
     * Constructs a new Board with the specified number of days and players.
     *
     * @param numDays the number of days for the game
     * @param players the list of players in the game
     */
    Board(int numDays, List<Player> players, String boardXMLFilePath, String cardsXMLFilePath) {
        this.numDays = numDays;
        this.numScenesRemaining = 10;
        this.players = players;
        this.deck = new Deck(cardsXMLFilePath);
        this.locations = new HashMap<String, Location>();
        this.boardXMLFilePath = boardXMLFilePath;
        initLocations(boardXMLFilePath);
        dealSceneCardsToLocations();
    }

    /**
     * Initializes the locations on the board.
     */
    void initLocations(String boardXMLFilePath) {
        ParseBoardXML parser = new ParseBoardXML();
        try {
            Document doc = parser.getDocFromFile(boardXMLFilePath);
            parser.readData(doc);
            this.locations = renameLocations(parser.getLocations());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Renames the "trailer" and "office" locations to "Trailer" and "Casting Office" respectively.
     */
    private Map<String, Location> renameLocations(Map<String, Location> locations) {
        // create a new map to store the updated locations
        Map<String, Location> updatedLocations = new HashMap<>(locations);
        // check if the locations map contains the key "trailer"
        if (locations.containsKey("trailer")) {
            Location trailer = locations.get("trailer");
            updatedLocations.remove("trailer");
            updatedLocations.put("Trailer", trailer);
        } // check if the locations map contains the key "office"
        if (locations.containsKey("office")) {
            Location office = locations.get("office");
            updatedLocations.remove("office");
            updatedLocations.put("Casting Office", office);
        }
        // clear the locations map and add all the updated locations
        locations.clear();
        locations.putAll(updatedLocations);

        return locations;
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
     * Returns the locations on the board.
     * 
     * @return The locations on the board.
     */
    Map<String, Location> getLocations() {
        return locations;
    }

    /**
     * Returns the number of shots remaining at a location.
     *  
     * @param location The location to check.
     * @return The number of shots remaining at the location.
     */
    int getLocationShotsRemaining(Location location) {
        return location.getShots();
    }

    /**
     * Wraps a shot at a location.
     * 
     * @param location The location to wrap a shot at.
     */
    void wrapLocationShot(Location location) {
        location.wrapShot();
    }

    /**
     * Returns the neighbors of a location.
     *
     * @param location The location to check.
     * @return The neighbors of the location.
     */
    List<String> getLocationNeighbors(Location location) {
        return location.getNeighbors();
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
        return location.getSceneCard();
    }

    /**
     * Deals new scene card to each location on the board.
     */
    void dealSceneCardsToLocations() {
        for (Location location : locations.values()) {
            SceneCard card = deck.drawCard();
            location.setSceneCard(card);
        }
    }

    /**
     * Resets the board to its initial state.
     */
    void resetBoard() {
        initLocations(this.boardXMLFilePath);
        dealSceneCardsToLocations();
        resetPlayerLocations();
    }

    // return player object 
    Player getPlayer(int ID) {
        for (Player player : players) {
            if (Integer.valueOf(player.getID()).equals(ID)) {
                return player;
            }
        }
        throw new IllegalArgumentException("No player " + ID);
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