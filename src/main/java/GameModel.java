import java.io.IOException;
import java.util.*;

/**
 * Represents the model of the game.
 * 
 * It manages the game state and data.
 * It initializes the game board, locations, deck, and players.
 * It also provides global access to the game state and data.
 */
public class GameModel implements Subject {
    private static transient GameModel instance = null;
    private transient List<Observer> observers = new ArrayList<>();
    private int numDays;
    private int currentDay = 1;
    private int numPlayers;
    private int currentPlayer = 1; // Player 1 starts the game
    private List<Player> players;
    private Deck deck;
    private Map<String, Location> locations;
    private Board board;


/************************************************************
 * Contructor
 ************************************************************/

    /**
     * Initializes a new Model.
     */
    private GameModel() {
        this.players = null;
        this.deck = null;
        this.locations = null;
        this.board = null;
    }


/************************************************************
 * Singleton Pattern
 ************************************************************/

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


/************************************************************
 * Initialization
 ************************************************************/

    /**
     * Initializes the model with the given number of players and XML file path.
     *
     * @param numPlayers The number of players.
     * @param boardXMLFilePath The file path to the board XML file.
     * @param cardsXMLFilePath The file path to the cards XML file.
     */
    public void initModel(int numPlayers, String boardXMLFilePath, String cardsXMLFilePath) {
        this.numPlayers = numPlayers;
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
        this.players = initPlayersList(numPlayers, playerRank, playerCredits);
    }

    /**
     * Initializes the list of players.
     *
     * @param numPlayers The number of players.
     * @param playerRank The rank of the players.
     * @param playerCredits The credits of the players.
     * @return The list of players.
     */
    private List<Player> initPlayersList(int numPlayers, int playerRank, int playerCredits) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            Player newPlayer = new Player(i + 1, playerRank, playerCredits);
            // newPlayer.setLocation(null); //placeholder until initBoard is run
            players.add(newPlayer);
        }
        return players;
    }

    /**
     * Initializes the deck.
     *
     * @param cardsXMLFilePath The XML file path for the cards.
     */
    private void initDeck(String cardsXMLFilePath) {
        List<SceneCard> cards = new ArrayList<>();
        ParseCardsXML parser = new ParseCardsXML();
        try {
            // Get the cards from the parser
            cards = parser.getCards(cardsXMLFilePath);
            // Create a new deck with the cards
            this.deck = new Deck(cards);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            this.locations = parser.getLocations(boardXMLFilePath);
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
     * @param players The list of players.
     * @param deck The deck of cards.
     * @param locations The map of locations.
     */
    private void initBoard(List<Player> players, Deck deck, Map<String, Location> locations) {
        // Create a new board
        this.board = new Board(deck, locations);
        // Set all player locations to Trailer
        for (Player player : players) {
            board.setPlayerLocation(player, "Trailer");
        }
    }

    
/************************************************************
 * Model State Management
 ************************************************************/

    /**
     * Loads the model state from a JSON file.
     * 
     * @param jsonFilePath Path to the JSON file.
     * @return True if the model was successfully loaded, false otherwise.
     * @throws IOException If an error occurs while loading the JSON file.
     */
    public static synchronized boolean loadFromJson(String jsonFilePath) {
        try {
            // Load the new state from the JSON file
            GameModel newModel = JsonUtil.loadFromJsonFile(jsonFilePath, GameModel.class);
            // Reset the singleton instance with the new state
            getInstance().loadModel(newModel);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Loads the GameModel data from the given GameModel.
     * @param newModel
     */
    private void loadModel(GameModel newModel) {
        this.numDays = newModel.numDays;
        this.currentDay = newModel.currentDay;
        this.numPlayers = newModel.numPlayers;
        this.currentPlayer = newModel.currentPlayer;
        this.players = newModel.players;
        this.deck = newModel.deck;
        this.locations = newModel.locations;
        this.board = newModel.board;
    }


/************************************************************
 * Day Management
 ************************************************************/

    /**
     * Returns the number of days.
     *
     * @return The number of days.
     */
    public int getNumDays() {
        return this.numDays;
    }

    /**
     * Returns the current day.
     *
     * @return The current day.
     */
    public int getDay() {
        return this.currentDay;
    }

    /**
     * Increments the current day.
     */
    public void incrementDay() {
        this.currentDay++;
    }

    
/************************************************************
 * Player Management
 ************************************************************/

    /**
     * Returns the number of players.
     *
     * @return The number of players.
     */
    public int getNumPlayers() {
        return this.numPlayers;
    }

    /**
     * Returns the current player.
     *
     * @return The current player.
     */
    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * Increments the current player.
     */
    public void setNextPlayerToCurrentPlayer() {
        this.currentPlayer++;
        if (this.currentPlayer > this.numPlayers) {
            this.currentPlayer = 1;
        }
    }

    /**
     * Returns the player with the given ID.
     *
     * @param ID The ID of the player.
     * @return The player with the given ID.
     * @throws IllegalArgumentException If no player with the given ID is found.
     */
    public Player getPlayer(int ID) {
        for (Player player : players) {
            if (Integer.valueOf(player.getID()).equals(ID)) {
                return player;
            }
        }
        throw new IllegalArgumentException("No player " + ID);
    }

    /**
     * Returns the active player.
     *
     * @return The active player.
     * @throws IllegalArgumentException If no active player is found.
     */
    public Player getActivePlayer() {
        for (Player player : players) {
            if (player.isActive()) {
                return player;
            }
        }
        throw new IllegalArgumentException("No active player");
    }

    /**
     * Returns the list of players.
     *
     * @return The list of players.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Change all player locations on the board to "Trailer"
     */
    public void resetPlayerLocations() {
        for (Player player : this.players) {
            // move player to trailer
            this.board.setPlayerLocation(player, "Trailer");
        }
    }

    /**
     * Resets all player roles to null
     */
    public void resetPlayerRoles() {
        for (Player player : this.players) {
            this.board.setPlayerRole(player.getID(), null);
        }
    }

    /**
     * Resets all player rehearsal tokens to 0
     */
    public void resetPlayerRehearsalTokens() {
        for (Player player : this.players) {
            player.resetRehearsalTokens();
        }
    }


/************************************************************
 * Game Element Accessors
 ************************************************************/

    /**
     * Returns the deck.
     *
     * @return The deck.
     */
    public Deck getDeck() {
        return deck;
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


/************************************************************
 * Observer Pattern
 ************************************************************/

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

}