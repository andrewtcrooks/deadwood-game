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
    private List<String> trailer_neighbors;
    private Location trailer;
    private List<String> office_neighbors;
    private Location office;

    /**
     * Constructs a new Board with the specified number of days and players.
     *
     * @param numDays the number of days for the game
     * @param players the list of players in the game
     * @param boardXMLFilePath the file path to the board XML file
     * @param cardsXMLFilePath the file path to the cards XML file
     */
    public Board(int numDays, List<Player> players, String boardXMLFilePath, String cardsXMLFilePath) {
        this.numDays = numDays;
        this.numScenesRemaining = 10;
        this.players = players;
        this.deck = new Deck(cardsXMLFilePath);
        this.locations = new HashMap<String, Location>();
        // Initialize locations on the board
        initLocations(boardXMLFilePath);
        this.trailer_neighbors = Arrays.asList("Main Street", "Saloon", "Hotel");
        this.trailer = new Location("Trailer", this.trailer_neighbors, new Area(0,0,0,0), Arrays.asList(), Arrays.asList());
        this.office_neighbors = Arrays.asList("Train Station", "Ranch", "Secret Hideout");
        this.office = new Location("Casting Office", this.office_neighbors, new Area(0,0,0,0), Arrays.asList(), Arrays.asList());
        this.locations.put("Trailer", this.trailer);
        this.locations.put("Casting Office", this.office);
        // Deal scene cards to locations
        dealSceneCardsToLocations();
    }

    /**
     * Returns the number of days remaining in the game.
     *
     * @return The number of days remaining in the game.
     */
    public int getNumDays() {
        return this.numDays;
    }

    /**
     * Decrements the number of days remaining in the game by 1.
     *
     * @return The new number of days remaining in the game.
     */
    public int decrementNumDays() {
        return --this.numDays;
    } 

    /**
     * Returns the number of scenes remaining in the game.
     *
     * @return The number of scenes remaining in the game.
     */
    public int getNumScenesRemaining() {
        return this.numScenesRemaining;
    }

    /**
     * Decrements the number of scenes remaining in the game by 1.
     *
     * @return The new number of scenes remaining in the game.
     */
    public int decrementNumScenesRemaining() {
        return --this.numScenesRemaining;
    }

    /**
     * Resets the number of scenes remaining in the game to 10.
     */
    public void resetNumScenesRemaining() {
        this.numScenesRemaining = 10;
    }

    /**
     * Returns the player with the given ID.
     * 
     * @param ID The ID of the player.
     * @throws IllegalArgumentException If no player with the given ID is found.
     * @return The player with the given ID.
     */
    public Player getPlayer(int ID) {
        for (Player player : this.players) {
            if (Integer.valueOf(player.getID()).equals(ID)) {
                return player;
            }
        }
        throw new IllegalArgumentException("No Player " + ID);
    }

    /**
     * Returns the players on the board.
     * 
     * @return The players on the board.
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Returns the locations on the board.
     * 
     * @return The locations on the board.
     */
    public Map<String, Location> getLocations() {
        return this.locations;
    }

    /**
     * Returns the number of shots remaining at a location.
     *  
     * @param location The location to check.
     * @return The number of shots remaining at the location.
     */
    public int getLocationShotsRemaining(Location location) {
        return location.getShots();
    }

    /**
     * Wraps a shot at a location.
     * 
     * @param location The location to wrap a shot at.
     */
    public void wrapLocationShot(Location location) {
        location.wrapShot();
    }

    /**
     * Returns the neighbors of a location.
     *
     * @param location The location to check.
     * @return The neighbors of the location.
     */
    public List<String> getLocationNeighbors(Location location) {
        return location.getNeighbors();
    }

    /**
     * Returns the roles at a location.
     *
     * @param location The location to check.
     * @return The roles at the location.
     */
    public List<Role> getLocationRoles(Location location) {
        return location.getRoles();
    }

    /**
     * Returns the scene card at a location.
     *
     * @param location The location to check.
     * @return The scene card at the location.
     */
    public SceneCard getLocationScene(Location location) {
        return location.getSceneCard();
    }

    /**
     * Resets the board to its initial state.
     */
    public void resetBoard() {
        resetPlayerLocations();
        dealSceneCardsToLocations();
    }

    /**
     * Initialize the locations on the board.
     * 
     * @param boardXMLFilePath The file path to the board XML file.
     * @throws Exception If an error occurs while parsing the board XML file.
     */
    private void initLocations(String boardXMLFilePath) {
        ParseBoardXML parser = new ParseBoardXML();
        try {
            Document doc = parser.getDocFromFile(boardXMLFilePath);
            parser.readData(doc);
            this.locations = parser.getLocations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deals new scene card to each location on the board.
     */
    private void dealSceneCardsToLocations() {
        for (Location location : this.locations.values()) {
            if (location.getName().equals("Trailer") || location.getName().equals("Casting Office")) {
                continue; // Skip the current iteration for these locations
            }
            SceneCard card = this.deck.drawCard();
            location.setSceneCard(card);
        }
    }

    /**
     * Change all player locations on the board to "Trailer"
     */
    private void resetPlayerLocations() {
        for (Player player : this.players) {
            // remove player from current location
            Location currentLocation = player.getLocation();
            currentLocation.removePlayer(player);
            // add trailer to player
            player.setLocation(locations.get("Trailer"));
            // add player to trailer
            locations.get("Trailer").addPlayer(player);
        }
    }
}