import java.io.Serializable;
import java.util.*;

/**
 * Represents the model of the game.
 * It manages the game state and data.
 * It initializes the game board, locations, deck, and players.
 * It also provides global access to the game state and data.
 */
public class GameModel implements Subject, Serializable {

    private static GameModel instance = null;
    private List<Observer> observers = new ArrayList<>();
    // private int numPlayers = 0;
    private int numDays;
    private int day = 1;
    private Board board;
    private Map<String, Location> locations;

    /**
     * Initializes a new Model.
     */
    private GameModel() {
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
        List <Player> players = initPlayers(numPlayers);
        initBoard(numPlayers, players, boardXMLFilePath, cardsXMLFilePath);
    }

    /**
     * Initializes the players.
     * 
     * @param numPlayers The number of players.
     * @throws IllegalArgumentException If the number of players is not between 2 and 8.
     */
    private List<Player> initPlayers(int numPlayers) {
        // Check if the number of players is between 2 and 8
        if (numPlayers < 2 || numPlayers > 8) {
            throw new IllegalArgumentException("Number of players must be between 2 and 8");
        }
        // Set the number of players
        // this.numPlayers = numPlayers;
        int playerRank = numPlayers < 7 ? 1 : 2; // 1 if numPlayers are less than 7, otherwise 2
        int playerCredits = (numPlayers == 5) ? 2 : (numPlayers == 6) ? 4 : 0; // 2 if numPlayers are 5, 
                                                                                // 4 if numPlayers are 6,
                                                                                // otherwise 0
        this.numDays = numPlayers < 4 ? 3 : 4; // 3 if numPlayers are less than 4, otherwise 4
        // return the initialized players
        return initPlayersList(numPlayers, playerRank, playerCredits);
    }

    /**
     * Initializes the players.
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
            newPlayer.setLocation(null); //placeholder until initBoard is run
            players.add(newPlayer);
        }
        return players;
    }

    /**
     * Initializes the board.
     *
     * @param numPlayers The number of players.
     * @param players The list of players.
     * @param boardXMLFilePath The XML file path for the board.
     * @param cardsXMLFilePath The XML file path for the cards.
     */
    private void initBoard(int numPlayers, List<Player> players, String boardXMLFilePath, String cardsXMLFilePath) {
        // Create a new board
        this.board = new Board(numPlayers, players, boardXMLFilePath, cardsXMLFilePath);
        // Set all player locations to Trailer
        for (Player player : players) {
            player.setLocation(board.getLocations().get("Trailer"));
        }
        this.locations = board.getLocations();
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