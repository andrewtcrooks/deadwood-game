import java.util.*;
import org.w3c.dom.Document;

/**
 * Represents the model of the game.
 * It manages the game state and data.
 * It initializes the game board, locations, deck, and players.
 * It also provides global access to the game state and data.
 */
public class GameModel implements Subject {

    private static transient GameModel instance = null;
    private transient List<Observer> observers = new ArrayList<>();
    private int numDays;
    private int day = 1;
    private Map<Integer, Player> players;
    private Deck deck;
    private Map<String, Location> locations;
    private Board board;

    /**
     * Initializes a new Model.
     */
    private GameModel() {
        this.players = null;
        this.deck = null;
        this.locations = null;
        this.board = null;
    }

    /**
     * Returns the instance of the Model.
     *
     * @return The instance of the Model.
     */
    public static synchronized GameModel getInstance() {
        if (instance == null) {
            instance = new GameModel();
        }
        return instance;
    }

    //TODO: comment out unless running Unit Tests
    /**
     * Resets the instance of the Model.
     */
    public static synchronized void reset() {
        instance = null;
    }

    /**
     * Initializes the model with the given number of players and XML file path.
     *
     * @param numPlayers The number of players.
     * @param xmlFilePath The XML file path.
     */
    public void initModel(int numPlayers, String boardXMLFilePath, String cardsXMLFilePath) {
        initPlayers(numPlayers);
        initDeck(cardsXMLFilePath);
        initLocations(boardXMLFilePath);
        initBoard(this.players, this.deck, this.locations);
    }

    /**
     * Initializes the players.
     * 
     * @param numPlayers The number of players.
     * @throws IllegalArgumentException If the number of players is not between 2 and 8.
     */
    private void initPlayers(int numPlayers) {
        // Check if the number of players is between 2 and 8
        if (numPlayers < 2 || numPlayers > 8) {
            throw new IllegalArgumentException("Number of players must be between 2 and 8");
        }
        int playerRank = numPlayers < 7 ? 1 : 2; // 1 if numPlayers are less than 7, otherwise 2
        int playerCredits = (numPlayers == 5) ? 2 : (numPlayers == 6) ? 4 : 0; // 2 if numPlayers are 5, 
                                                                                // 4 if numPlayers are 6,
                                                                                // otherwise 0
        this.numDays = numPlayers < 4 ? 3 : 4; // 3 if numPlayers are less than 4, otherwise 4
        // Initialize the players
        this.players = initPlayersMap(numPlayers, playerRank, playerCredits);
    }

    /**
     * Initializes the players.
     *
     * @param numPlayers The number of players.
     * @param playerRank The rank of the players.
     * @param playerCredits The credits of the players.
     * @return The map of players.
     */
    private Map<Integer, Player> initPlayersMap(int numPlayers, int playerRank, int playerCredits) {
        Map<Integer, Player> playersMap = new HashMap<>();
        for (int i = 0; i < numPlayers; i++) {
            Player newPlayer = new Player(i + 1, playerRank, playerCredits);
            // newPlayer.setLocation(null); //placeholder until initBoard is run
            playersMap.put(i + 1, newPlayer);
        }
        return playersMap;
    }

    /**
     * Initializes the deck.
     *
     * @param cardsXMLFilePath The XML file path for the cards.
     */
    private void initDeck(String cardsXMLFilePath) {
        this.deck = new Deck(cardsXMLFilePath);
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
        List<String> trailer_neighbors = Arrays.asList("Main Street", "Saloon", "Hotel");
        Location trailer = new Location("Trailer", trailer_neighbors, new Area(0,0,0,0), Arrays.asList(), Arrays.asList());
        List<String> office_neighbors = Arrays.asList("Train Station", "Ranch", "Secret Hideout");
        Location office = new Location("Casting Office", office_neighbors, new Area(0,0,0,0), Arrays.asList(), Arrays.asList());
        this.locations.put("Trailer", trailer);
        this.locations.put("Casting Office", office);
    }

    /**
     * Initializes the board.
     *
     * @param numPlayers The number of players.
     * @param players The list of players.
     * @param deck The deck of cards.
     * @param locations The map of locations.
     */
    private void initBoard(Map<Integer, Player> players, Deck deck, Map<String, Location> locations) {
        // Create a new board
        this.board = new Board(deck, locations);
        // Set all player locations to Trailer
        for (Map.Entry<Integer, Player> entry : players.entrySet()) {
            board.setPlayerLocation(entry.getValue(), "Trailer");
        }
    }

    /**
     * Registers an observer with the model.
     *
     * @param observer The observer to register.
     */
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer from the list of observers.
     *
     * @param observer The observer to remove.
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers that the state has changed.
     */
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    /**
     * Returns the deck.
     *
     * @return The deck.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Returns the locations.
     *
     * @return The locations.
     */
    public Map<String,Location> getLocations() {
        return locations;
    }

    /**
     * Returns the board.
     *
     * @return The board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the number of days.
     *
     * @return The number of days.
     */
    public int getNumDays() {
        return this.numDays;
    }

    /**
     * Returns the player with the given ID.
     *
     * @param ID The ID of the player.
     * @return The player with the given ID.
     * @throws IllegalArgumentException If no player with the given ID is found.
     */
    public Player getPlayer(int ID) {
        if (players.containsKey(ID)) {
            return players.get(ID);
        }
        throw new IllegalArgumentException("No player with ID: " + ID);
    }

    /**
     * Returns the map of player IDs to Player objects.
     *
     * @return The map of player IDs to Player objects.
     */
    public Map<Integer, Player> getPlayers() {
        return new HashMap<>(players);
    }

    /**
     * Change all player locations on the board to "Trailer"
     */
    public void resetPlayerLocations() {
        for (Map.Entry<Integer, Player> entry : this.players.entrySet()) {
            // move player to trailer
            this.board.setPlayerLocation(entry.getValue(), "Trailer");
        }
    }
    /**
     * Returns the location with the given name.
     *
     * @param name The name of the location.
     * @return The location with the given name.
     * @throws IllegalArgumentException If no location with the given name is found.
     */
    Location getLocation(String name) {
        if (locations.containsKey(name)) {
            return locations.get(name);
        }
        throw new IllegalArgumentException("No location " + name);
    }
    
    /**
     * Sets the model to the given model. Used for loading saved game state.
     *
     * @param model The model to set.
     */
    public void setModel(GameModel model) {
        // set the model to the given model
        instance = model;
        // update views
        notifyObservers();
    }

    /**
     * Increments the current day.
     */
    public void incrementDay() {
        this.day++;
    }

    /**
     * Returns the current day.
     *
     * @return The current day.
     */
    public int getDay() {
        return this.day;
    }


}