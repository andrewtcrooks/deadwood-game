import java.util.*;
import java.util.stream.Collectors;

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
    private List<Player> players;
    private Deck deck;
    private Map<String, Location> locations;
    private List<String> trailer_neighbors;
    private Location trailer;
    private List<String> office_neighbors;
    private Location office;
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
        this.players = initPlayersList(numPlayers, playerRank, playerCredits);
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
        this.trailer_neighbors = Arrays.asList("Main Street", "Saloon", "Hotel");
        this.trailer = new Location("Trailer", this.trailer_neighbors, new Area(0,0,0,0), Arrays.asList(), Arrays.asList());
        this.office_neighbors = Arrays.asList("Train Station", "Ranch", "Secret Hideout");
        this.office = new Location("Casting Office", this.office_neighbors, new Area(0,0,0,0), Arrays.asList(), Arrays.asList());
        this.locations.put("Trailer", this.trailer);
        this.locations.put("Casting Office", this.office);
    }

    /**
     * Initializes the board.
     *
     * @param numPlayers The number of players.
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
     * Returns the player with the given ID.
     *
     * @param ID The ID of the player.
     * @return The player with the given ID.
     * @throws IllegalArgumentException If no player with the given ID is found.
     */
    Player getPlayer(int ID) {
        for (Player player : players) {
            if (Integer.valueOf(player.getID()).equals(ID)) {
                return player;
            }
        }
        throw new IllegalArgumentException("No player " + ID);
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

    /**
     * Wraps the scene in the Location.
     */
    public void wrapScene(Location location) {
        // Check for any player working a role on a card
        List<Player> playersAtLocation = getPlayers();
        boolean anyPlayerOnCard = playersAtLocation.stream()
            .anyMatch(player -> player.getRole() != null && player.getRole().getOnCard());
        // Pay out bonus if any player was on a card
        if (anyPlayerOnCard) {
            payOutBonus(location);
        }
        // Remove all players from their roles and reset rehearsal tokens
        playersAtLocation.forEach(player -> {
            player.leaveRole();
            player.resetRehearsalTokens();
        });
        // Reset takes
        location.resetTakes();
        // Clear the scene card
        location.clearSceneCard();
        // set wrapped to true
        location.setSceneWrapped();
    }

    /**
     * Pays out the bonus to all players at the Location.
     */
    private void payOutBonus(Location location) {
        int movieBudget = location.getSceneCard().getBudget(); // Get the movie budget
        List<Integer> diceRolls = rollDice(movieBudget); // Roll dice equal to the budget
        Collections.sort(diceRolls, Collections.reverseOrder()); // Sort dice rolls in descending order

        // Get all players on the card, sorted by their role rank in descending order
        List<Player> playersOnCard = getPlayers().stream()
            .filter(player -> player.getRole() != null && player.getRole().getOnCard())
            .sorted(Comparator.comparingInt(Player::getRoleRank).reversed())
            .collect(Collectors.toList());
        // Distribute dice rolls in a round-robin fashion
        for (int i = 0; i < diceRolls.size(); i++) {
            Player player = playersOnCard.get(i % playersOnCard.size()); // Wrap around if more dice than players
            player.addMoney(diceRolls.get(i)); // Add the dice roll as a bonus
        }
        // Players leave their roles after receiving bonuses
        playersOnCard.forEach(Player::leaveRole);
        // Pay each player who has a role but is not on the card an amount equal to the rank of the role they are in
        getPlayers().stream()
        .filter(player -> player.getRole() != null && !player.getRole().getOnCard())
        .forEach(player -> player.addMoney(player.getRole().getRank()));
    }

    /**
     * Rolls a number of dice and returns the results.
     *
     * @param numDice the number of dice to roll
     * @return the results of the dice rolls
     */
    private List<Integer> rollDice(int numDice) {
        List<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < numDice; i++) {
            Dice dice = new Dice(); // Use the Dice class to roll the dice
            rolls.add(dice.getValue()); // Get the value of the roll
        }
        return rolls;
    }

}