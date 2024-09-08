import java.util.*;
import javafx.application.Platform;
// import javafx.scene.input.MouseEvent;
// import javafx.scene.input.MouseButton;
// import javafx.scene.Node;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.layout.Pane;
// import javafx.scene.layout.StackPane;
// import javafx.scene.layout.VBox;

/**
 * Represents the game controller for the game.
 * It manages the game flow.
 */
public class GameController implements GameActionListener{
    private GameModel model;
    private GameView view;
    private Runnable onGameInitialized;
    private List<Area> areasWithListeners;
    private static final HashMap<String, PlayerAction> actionMap = 
        new HashMap<>();
    static {
        actionMap.put("who", new PlayerActionWho());
        actionMap.put("where", new PlayerActionWhere());
        actionMap.put("board", new PlayerActionBoard());
        actionMap.put("move", new PlayerActionMove());
        actionMap.put("work", new PlayerActionWork());
        actionMap.put("upgrade", new PlayerActionUpgrade());
        actionMap.put("rehearse", new PlayerActionRehearse());
        actionMap.put("act", new PlayerActionAct());
        actionMap.put("end", new PlayerActionEnd());
        actionMap.put("quit", new PlayerActionQuit());
        actionMap.put("save", new PlayerActionSave());
        actionMap.put("load", new PlayerActionLoad());
        actionMap.put("help", new PlayerActionHelp());
    }


// Constructor and Initialization


    /**
     * Constructs a new GameController.
     */
    public GameController () {
        super();
        this.model = null;
        this.view = null;
        this.areasWithListeners = new ArrayList<>();
    }

    /**
     * Initializes the game with the necessary settings, including setting up
     * the model and view, and getting the number of players to initialize in the
     * model.
     * 
     * @param model
     * @param view
     * @param boardXMLFilePath
     * @param cardsXMLFilePath
     */
    public void initializeGame(
        GameModel model,
        GameView view,
        String boardXMLFilePath,
        String cardsXMLFilePath
    ) {
        this.model = model;
        this.view = view;
    
        // UI operations related to player input
        Platform.runLater(() -> {
            int numPlayers = this.view.getNumPlayers();  // Ask the user for the number of players
    
            // Start initialization after user input is valid
            initializeModelAndGame(numPlayers, boardXMLFilePath, cardsXMLFilePath);
        });
    }

    private void initializeModelAndGame(int numPlayers, String boardXMLFilePath, String cardsXMLFilePath) {
        // Do non-UI game initialization in a separate thread
        Platform.runLater(() -> {
            // repaint the board
            this.model.notifyObservers("SHOW_BOARD", null);

            // Initialize the model
            this.model.initModel(numPlayers, boardXMLFilePath, cardsXMLFilePath);

            // Initialize player dice in the view (layer 3)
            initializePlayerDice();

            // Initialize location cards(layer 1) in the view underneath 
            // cardbacks (layer 2)
            initializeLocationCards();

            // Create player stats table
            this.model.notifyObservers("CREATE_PLAYER_STATS_TABLE", numPlayers);

            // Update player stats
            updateAllPlayerStats();

            // Set the controller as the game action listener for the view
            this.view.setGameActionListener(this);

            // Notify that the game is ready to start
            if (onGameInitialized != null) {
                onGameInitialized.run();
            }
        });
    }

    // public void initializeGame(
    //     GameModel model, 
    //     GameView view, 
    //     String boardXMLFilePath, 
    //     String cardsXMLFilePath
    // ) {
    //     this.model = model;
    //     this.view = view;

    //     // Do long-running work off the JavaFX thread
    //     new Thread(() -> {
            
            
    //         // Game initialization logic (off the main thread)
    //         Platform.runLater(() -> {
                
    //             int numPlayers = 0;

    //             // Get the number of players and initialize the model
    //             boolean initializationSuccessful = false;
    //             while (!initializationSuccessful) {
    //                 try {
    //                     numPlayers = this.view.getNumPlayers();
    //                     initializationSuccessful = true; // no exception was thrown
    //                 } catch (IllegalArgumentException e) {
    //                     this.view.showMessage(
    //                         "Invalid number of players: " + 
    //                         e.getMessage()
    //                     );
    //                     this.model.notifyObservers(
    //                         "SHOW_MESSAGE", 
    //                         "Invalid command. Please try again."
    //                     );
    //                     // Loop will continue until valid number of players is provided
    //                 }
    //             }

    //             // repaint the board
    //             this.model.notifyObservers("SHOW_BOARD", null);
                
    //             // Initialize the model
    //             this.model.initModel(numPlayers, boardXMLFilePath, cardsXMLFilePath);

    //             // Initialize player dice in the view (layer 3)
    //             initializePlayerDice();

    //             // Initialize location cards(layer 1) in the view underneath 
    //             // cardbacks (layer 2)
    //             initializeLocationCards();

    //             // Create player stats table
    //             this.model.notifyObservers("CREATE_PLAYER_STATS_TABLE", numPlayers);

    //             // Update player stats
    //             updateAllPlayerStats();

    //             // Set the controller as the game action listener for the view
    //             this.view.setGameActionListener(this);

    //             // Notify that the game is ready to start
    //             if (onGameInitialized != null) {
    //                 onGameInitialized.run();
    //             }

    //         });
    //     }).start();
    // }

    /**
     * Sets the onGameInitialized Runnable.
     * 
     * @param onGameInitialized The Runnable to set.
     */
    public void setOnGameInitialized(Runnable onGameInitialized) {
        this.onGameInitialized = onGameInitialized;
    }


// Game Flow Management


    /**
     * Starts the game and manages the game flow after the game is initialized.
     */
    public void playDays() {
        // start the game
        int numDays = this.model.getNumDays();
        int day = this.model.getDay();
        while (day <= numDays) {
            // play day
            playDay();
            // end day
            endDay();
            // increment day by one in model
            this.model.incrementDay();
            // update day
            day = this.model.getDay();
        }
    }

    /**
     * Manages a day in the game.
     */
    private void playDay() {
        // Display the beginning of the day message
        this.model.notifyObservers(
            "SHOW_MESSAGE",
            "Day " + (model.getDay()) + " has begun."
        );
        // cycle through each player's turn until only 1 scene is left
        while (this.model.getBoard().getNumScenesRemaining() > 1) {
            // manage the player's turn
            playerTurn(this.model.getCurrentPlayer());
            // set current player to the next player
            this.model.setNextPlayerToCurrentPlayer();
        }
    }

    /**
     * Manages a player's turn in the game.
     * 
     * @param ID The ID of the player whose turn it is.
     */
    private void playerTurn(int ID) {
        boolean endTurn = false;
        Player player = this.model.getPlayer(ID);
        player.setActive(true);
        player.setHasMoved(false);
        player.setHasWorked(false);
        player.setHasUpgraded(false);

        while (!endTurn) {

            if (view instanceof GameGUIView) {
                onPlayerTurnStart();
            }

            // Get the player's input
            String command = this.view.getPlayerInput();

            // Execute the command for the player
            if (view instanceof GameCLIView) {
                endTurn = executeCommand(command, player);
            } else if (view instanceof GameGUIView) {
                // don't worry about use executeCommand to validate inputs
                onPlayerTurnEnd();
            }

        }

        // Set player to inactive
        player.setActive(false);

    }

    /**
     * Handles the beginning of each player's turn.
     */
    public void onPlayerTurnStart() {
        int currentPlayerID = this.model.getCurrentPlayer();
        Player currentPlayer = this.model.getPlayer(currentPlayerID);
        Board board = this.model.getBoard();
        // get player's string location name
        String locationName = board.getPlayerLocationName(currentPlayer);

        if (board.getPlayerRole(currentPlayerID) == null) {
            // Add neighbor location buttons


            // if at Trailer
            if (locationName.equals("Trailer")) {
                // Add move buttons to the neighbors
                addMoveListenersToNeighbors(currentPlayer);
            } // else if at Casting Office
            else if (locationName.equals("Casting Office")) {
                // Add move buttons to the neighbors
                // addMoveListenersToNeighbors(currentPlayer);
            } else {
                // Add location button to the current location
                // addWorkListenersToAvailableRoles(currentPlayer);
                addMoveListenerToCurrentLocation(currentPlayer);
            }

        } else {
            // Add location button to the current location
            addMoveListenerToCurrentLocation(currentPlayer);
        }
    }

    /**
     * Handles the end of each player's turn.
     */
    public void onPlayerTurnEnd() {
        int currentPlayerID = this.model.getCurrentPlayer();
        Player currentPlayer = this.model.getPlayer(currentPlayerID);
        // Remove all mouse listeners added at the start of the turn
        removeAllMouseListeners(currentPlayer);
    }

    private void addMoveListenerToCurrentLocation(Player player) {
        String locationName = this.model.getBoard().getPlayerLocationName(player);
        Location location = this.model.getLocation(locationName);
        Area area = location.getArea();
        // Create a HashMap to hold the event data
        Map<String, Object> eventData = new HashMap<>();
        // add data to eventData
        eventData.put("location", area);
        eventData.put("command", "rehearse");
        // eventData.put("data", null);


        this.model.notifyObservers("ADD_CLICKABLE_AREAS", eventData);
        areasWithListeners.add(area);
    }

    public void addMoveListenersToNeighbors(Player player) {
        String locationName = this.model.getBoard().getPlayerLocationName(player);
        Location location = this.model.getLocation(locationName);
        List<String> neighbors = location.getNeighbors();

        for (String neighborName : neighbors) {
            Location neighborLocation = this.model.getLocation(neighborName);
            // Location neighbor = this.model.getLocation(neighborName);
            Area area = neighborLocation.getArea();
            areasWithListeners.add(area);
            Map<String, Object> eventData = new HashMap<>();
            // Add data to eventData
            eventData.put("command", "MOVE");
            eventData.put("data", neighborName);
            eventData.put("area", area);
            // Add the neighbor location to the view
            this.model.notifyObservers("ADD_CLICKABLE_AREA", eventData);
        }
    }
    
    /**
     * Executes a command for a player.
     * 
     * @param command The command to execute.
     * @param player The player executing the command.
     * @return true if the command ends the player's turn, false otherwise.
     */
    public boolean executeCommand(String command, Player player) {
        // Get the action associated with the command
        PlayerAction action = actionMap.get(command);
        // Continue if input command not recognized
        if (action == null) {
            this.model.notifyObservers(
                "SHOW_MESSAGE", 
                "Command not recognized. Please try again."
            );
            return false;
        }
        // Validate and potentially execute the action
        if (action.validate(player, model, view)) {
            // If execute returns true, the current player's turn ends
            return action.execute(player, model, view);
        }
        // If command was "load", set the current player to active player
        if (command.equals("load")) {
            // Reset the current player to the reloaded model's active player
            player = this.model.getActivePlayer();
        }
        return false;
    }

    /**
     * Ends the day by resetting the board for the next day.
     */
    private void endDay() {
        // reset player locations to Trailer
        this.model.resetPlayerLocations();
        // reset player roles
        this.model.resetPlayerRoles();
        // reset player rehearsal tokens
        this.model.resetPlayerRehearsalTokens();
        // get deck
        Deck deck = this.model.getDeck();
        // discard the one remaining scene card from the board
        deck.discardLastDrawnCard();
        // get locations
        Map<String, Location> locations = this.model.getLocations();
        // reset the board for the next day by resetting shot counters 
        // and dealing enw scene cards
        this.model.getBoard().resetBoard(deck, locations);
    }

    /**
     * Scores the game and displays the final results.
     */
    public void scoreGame() {
        // Get the players
        List<Player> players = this.model.getPlayers();
        // Sort players by score and then by ID if scores are tied
        players.sort(
            Comparator.comparingInt(Player::getScore)
                      .reversed()
                      .thenComparingInt(player -> player.getID())
        );
        // Print game over screen
        this.model.notifyObservers(
            "SHOW_MESSAGE", 
            "Game over!"
        );
        // declare player(s) with the highest score the winner
        int highestScore = players.get(0).getScore();
        List<Player> winners = new ArrayList<>();
        for (Player player : players) {
            if (player.getScore() == highestScore) {
                winners.add(player);
            } else {
                break;
            }
        }
        // Print scores and indicate the winner(s)
        for (Player player : players) {
            String winnerIndicator = 
                player.getScore() == highestScore ? "  !Winner!" : "";
            this.model.notifyObservers(
                "SHOW_MESSAGE", 
                "Player ID: " + player.getID() + ", Score: " + 
                player.getScore() + winnerIndicator
            );
        }
    }


// Game Action Listener Methods


    /**
     * Handles the Move action.
    */
    @Override
    public void onMove(String location) {
        // Update the menu to show locations the player can select
        int currentPlayerID = this.model.getCurrentPlayer();
        Player currentPlayer = this.model.getPlayer(currentPlayerID);

        // move player to location
        movePlayerToLocation(currentPlayerID, location);

        // set player as hasMoved
        currentPlayer.setHasMoved(true);

    }

    /**
     * Handles the Work action.
     */
    public void onWork(){
        // Handle move action TODO: Remove line below
        this.model.notifyObservers(
            "SHOW_MESSAGE", 
            "Work is Selected"
        );
        // Update the menu to show locations the player can select
        int currentPlayerID = this.model.getCurrentPlayer();
        Player currentPlayer = this.model.getPlayer(currentPlayerID);
        String locationString = this.model.getBoard()
                                          .getPlayerLocationName(currentPlayer);
        Location location = this.model.getLocation(locationString);
        List<Role> locationRoles = location.getRoles();
        List<Role> cardRoles = this.model.getBoard()
                                         .getLocationSceneCardRoles(
                                            locationString, 
                                            this.model.getDeck()
                                         );
        locationRoles.addAll(cardRoles);
        // Notify the view to update the menu to the move menu
        this.model.notifyObservers("UPDATE_WORK_MENU", locationRoles);
    }

    /**
     * Handles the End action.
     */
    public void onEnd(){
        int currentPlayerID = this.model.getCurrentPlayer();
        Player currentPlayer = this.model.getPlayer(currentPlayerID);
        executeCommand("end", currentPlayer);
    }


    public void onSelectNeighbor(String locationString){
        // Update the menu to show locations the player can select
        int currentPlayerID = this.model.getCurrentPlayer();
        // Check if the locationString is "Cancel"

        // get location from locationString
        Location location = this.model.getLocation(locationString);
        // get location area
        Area area = location.getArea();
        int X = area.getX();
        int Y = area.getY();
        // Create a HashMap to hold the event data
        HashMap<String, Object> eventData = new HashMap<>();
        eventData.put("playerID", currentPlayerID);
        eventData.put("locationX", X);
        eventData.put("locationY", Y);
        this.model.notifyObservers("MOVE_TO_LOCATION", eventData);
    }


// Observer Pattern Methods


    private void initializePlayerDice() {
        // Constants for calculating player dice placement
        int GENERAL_Y_OFFSET = 40;
        int TRAILER_CASTING_X_OFFSET = 47;
        int TRAILER_CASTING_Y_OFFSET = 47;
        Board board = this.model.getBoard();

        // Make a Map to hold data for initializing player dice
        Map<Integer, Map<String, Integer>> initDiceLabels = new HashMap<>();
        for (Player player : this.model.getPlayers()) {
            int playerID = player.getID();
            String locationString = board.getPlayerLocationName(player);
            Location location = this.model.getLocation(locationString);
            Area area = location.getArea();
            int x = area.getX();
            int y = area.getY();
            Map<String, Integer> playerInfo = new HashMap<>();
            playerInfo.put("playerRank", player.getRank());

            if (
                locationString.equals("Trailer") || 
                locationString.equals("Casting Office")
            ) {
                // Calculate row and column for Trailer and Casting Office
                int row = (playerID - 1) / 4;
                int col = (playerID - 1) % 4;
                x += col * TRAILER_CASTING_X_OFFSET;
                y += row * TRAILER_CASTING_Y_OFFSET;
            } else {
                // Apply general Y offset for other locations
                y += playerID * GENERAL_Y_OFFSET;
            }
            // Add player location to playerInfo
            playerInfo.put("locationX", x);
            playerInfo.put("locationY", y);
            initDiceLabels.put(playerID, playerInfo);
        }

        // Initialize the player dice in the view
        this.model.notifyObservers("INIT_DICE_LABELS", initDiceLabels);
    }

    public void initializeLocationCards(){
        Board board = this.model.getBoard();
        // Make a Map to hold card ID and x and y coords for the scene cards
        Map<String, Integer> addCard = new HashMap<>();
        // Make a Map to hold the x and y coords for the card backs
        Map<String, Integer> addCardBacks = new HashMap<>();
        // Make a Map to hold the Locations
        Map<String, Location> locations = this.model.getLocations();
        // Add Scene cards to board
        for (Location location : locations.values()) {
            if (
                !location.getName().equals("Trailer") && 
                !location.getName().equals("Casting Office")
            ) {
                // get the x and y coords and scene card ID for the location
                Area area = location.getArea();
                int x = area.getX();
                int y = area.getY();
                int sceneCardID = board.getLocationSceneCardID(location
                                       .getName());
                if (sceneCardID != 0) { 
                    addCardBacks.put("locationX", x);
                    addCardBacks.put("locationY", y);
                    addCard.put("sceneCardID", sceneCardID);
                    addCard.put("locationX", x);
                    addCard.put("locationY", y);
                    this.model.notifyObservers("ADD_CARD_BACKS", addCardBacks);
                    this.model.notifyObservers("ADD_CARD", addCard);
                }
            }
        }
    }

    /**
     * Updates the player stats in the view.
     */
    public void updateAllPlayerStats() {
        // Collect player stats
        Map<Integer, List<Integer>> playerStats = new HashMap<>();
        for (Player player : model.getPlayers()) {
            List<Integer> stats = new ArrayList<>();
            stats.add(player.getMoney());
            stats.add(player.getCredits());
            stats.add(player.getRehearsalTokens());
            playerStats.put(player.getID(), stats);
        }
        // Send the player stats in the view
        this.model.notifyObservers("UPDATE_ALL_PLAYER_STATS", playerStats);
    }


        //     // Make a Map to hold the Area for each location that is not the Trailer or Casting Office
        //     Map<String, Area> locationAreas = new HashMap<>();
        //     // Put the Area for each location that is not the Trailer or Casting Office into the Map
        //     for (Location location : this.model.getLocations().values()) {
        //         if (!location.getName().equals("Trailer") && !location.getName().equals("Casting Office")) {
        //             locationAreas.put(location.getName(), location.getArea());
        //         }
        //     }
    
        //     // Initialize the card backs in the view
        //     this.model.notifyObservers("ADD_CARD_BACKS", locationAreas);
        // }

    /**
     * Updates the Move menu with the neighboring locations.
     * 
     * @param locationName The name of the location to get the neighbors for.
     */
    public void getMoveMenu(String locationName) {
        Location location = this.model.getLocation(locationName);
        this.model.notifyObservers(
            "SHOW_MOVE_MENU", 
            new ArrayList<>(location.getNeighbors())
        );
    }

    public void addMoveListenerToPlayerDie(Player player) {
        // Get the player's location
        String locationName = this.model.getBoard().getPlayerLocationName(player);
        Location location = this.model.getLocation(locationName);
        Area area = location.getArea();

        Map<String, Area> eventData = new HashMap<>();
        // Add the player die to the location
        this.model.notifyObservers("ADD_CLICKABLE_AREAS", area);
        areasWithListeners.add(area);
    }

    private void removeAllMouseListeners(Player player) {
        // Remove all mouse listeners added at the start of the turn
        for (Area area : areasWithListeners) {
            this.model.notifyObservers("REMOVE_CLICKABLE_AREAS", area);
        }
        // Clear the list for the next turn
        areasWithListeners.clear();
    }

    /**
     * Moves the player to the selected location.
     * 
     * @param playerID The ID of the player to move.
     * @param locationName The name of the location to move to.
     */
    public void movePlayerToLocation(int playerID, String locationName) {
        // Update the model with the player's new location
        Player player = this.model.getPlayer(playerID);
        Location location = this.model.getLocation(locationName);
        Board board = this.model.getBoard();
        board.setPlayerLocation(player, locationName);
        
        // Create and populate the movePlayer HashMap
        HashMap<String, Object> eventData = new HashMap<>();
        eventData.put("playerID", player.getID());
        // movePlayer.put("playerRank", player.getRank());
        eventData.put("locationX", location.getArea().getX());
        eventData.put("locationY", location.getArea().getY());
        
        // Pass the string and the movePlayer HashMap to notifyObservers
        this.model.notifyObservers("MOVE_TO_LOCATION", eventData);
    }

}
