import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;

import java.util.Comparator;

/**
 * Represents the game controller for the game.
 * It manages the game flow.
 */
public class GameController{
    private GameModel model;
    private GameView view;
    private Player currentPlayer;
    // private List<Area> areasWithListeners;
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
        this.model = null;
        this.view = null;
        // this.areasWithListeners = new ArrayList<>();
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
  
        // CLI Mode: Get the number of players and init model
        if (view instanceof GameCLIView) {
            int numPlayers = this.view.getNumPlayers();
            this.model.initModel(numPlayers, boardXMLFilePath, cardsXMLFilePath);
        } 
        // else{
        //     // Call setGameActionListener to ensure ButtonManager is initialized
        //     ((GameGUIView) view).setGameActionListener(this);
        // }
    }
    

    // Create the board and all elements based on the model
    public void createBoardElements() {
        // Initialize player dice, stats, etc., for the GUI
        initializeLocationCards();
        initializePlayerDice();
        this.model.notifyObservers(
            "CREATE_PLAYER_STATS_TABLE", 
            model.getPlayers().size()
        );
        updateAllPlayerStats();
                
    }


// Game Flow Management


    // /**
    //  * Returns a completable future when playDays is run in GUI mode.
    //  * @return
    //  */
    // public CompletableFuture<Integer> getPlayDaysFuture() {
    //     CompletableFuture<Integer> future = new CompletableFuture<>();
    //     playDays();
    //     future.complete(null);
    //     return future;
    // }

    /**
     * Manages the days in the game.
     */
    public void playDays() {
        int numDays = this.model.getNumDays();
        int day = this.model.getDay();
        if (day > numDays) {
            return;// If the current day exceeds numDays, exit playDays loop
        }

        // Play the day and end the day
        playDay();
        endDay();

        // Increment day in the model and recursively call playDays with new day
        this.model.incrementDay();
        playDays();
    }

    /**
     * Manages a day in the game.
     */
    private void playDay() {
        // Display the beginning of the day message
        this.model.notifyObservers(
            "SHOW_MESSAGE", 
            "Day " + model.getDay() + " has begun."
        );
        
        // Check if there is more than 1 scene remaining
        if (this.model.getBoard().getNumScenesRemaining() > 1) {
            // Manage the player's turn
            playerTurn(this.model.getCurrentPlayer());
            // Set next player and call playDay recursively
            this.model.setNextPlayerToCurrentPlayer();
            playDay(); // Recurse until only 1 scene is left
        }
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
     * Manages a player's turn in the game.
     * 
     * @param ID The ID of the player whose turn it is.
     */
    private void playerTurn(int ID) {
        Player player = this.model.getPlayer(ID);
        player.setActive(true);
        player.setHasMoved(false);
        player.setHasWorked(false);
        player.setHasUpgraded(false);

        // TODO: add the below bit for highlgihting row to GUI method
        // // GUI ONLY: Highlight the player's row in the player stats table
        // if (view instanceof GameGUIView) {
        //     ((GameGUIView) view).getPlayerStatsManager().highlightRow(player.getID());
        //     processPlayerTurnGUI(player);
        // } else {
        //     processPlayerTurn(player);
        // }
        processPlayerTurn(player);
    }

    /**
     * Handles the player's actions and determines if the turn ends.
     * 
     * @param player The player whose turn it is.
     */
    private void processPlayerTurn(Player player) {
        // Get the player's input
        String command = this.view.getPlayerInput();
        // Get the action associated with the command
        PlayerAction action = actionMap.get(command);

        // Continue if input command not recognized
        if (action == null) {
            this.model.notifyObservers("SHOW_MESSAGE", "Command not recognized. Please try again.");
            processPlayerTurn(player); // Recurse until valid command
            return;
        }

        // Validate and potentially execute the action      
        if (action.validate(player, model, view)) {
            boolean endTurn = action.execute(player, model, view);
            if (endTurn) {
                player.setActive(false); // End player's turn
                return;
            }
        }

        // If command was "load", set the current player to the active player
        if (command.equals("load")) {
            player = this.model.getActivePlayer(); // Reload player state
        }

        processPlayerTurn(player); // Recurse to continue turn
    }


    /**
     * Manages the days in the game asynchronously for the GUI version.
     */
    public CompletableFuture<Void> playDaysGUI() {
        int numDays = this.model.getNumDays();
        int day = this.model.getDay();

        if (day > numDays) {
            // Complete the game when all days are done
            return CompletableFuture.completedFuture(null);
        }

        return playDayGUI().thenCompose(ignored -> {
            // After the day ends, increment the day and move to the next
            this.model.incrementDay();

            // Recursively play the next day
            return playDaysGUI();
        });
    }

    /**
     * Manages a day in the game asynchronously for the GUI version.
     */
    private CompletableFuture<Void> playDayGUI() {
        // Display the beginning of the day message
        this.model.notifyObservers("SHOW_MESSAGE", "Day " + model.getDay() + " has begun.");

        // Check if more than 1 scene remains to continue the day
        if (this.model.getBoard().getNumScenesRemaining() > 1) {
            // Process the turns for all players during this day in GUI mode
            return processTurnsForAllPlayersGUI();
        } else {
            // If fewer than 1 scene remains, end the day
            endDayGUI();
            return CompletableFuture.completedFuture(null); // Day is complete
        }
    }

    /**
     * Processes the turns for all players sequentially in GUI mode using CompletableFutures.
     * @return CompletableFuture that completes when all players have taken their turns.
     */
    private CompletableFuture<Void> processTurnsForAllPlayersGUI() {
        List<Player> players = model.getPlayers();
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

        // Process each player's turn sequentially using CompletableFuture chaining
        for (Player player : players) {
            future = future.thenCompose(ignored -> playerTurnGUI(player.getID()));
        }

        return future; // Return a CompletableFuture that completes when all players have taken their turns
    }

    /**
     * Manages a player's turn in the game asynchronously for the GUI version.
     * 
     * @param ID The ID of the player whose turn it is.
     * @return CompletableFuture that completes when the player's turn ends.
     */
    private CompletableFuture<Void> playerTurnGUI(int ID) {
        Player player = this.model.getPlayer(ID);
        player.setActive(true);
        player.setHasMoved(false);
        player.setHasWorked(false);
        player.setHasUpgraded(false);

        // GUI ONLY: Highlight the player's row in the player stats table
        if (view instanceof GameGUIView) {
            ((GameGUIView) view).getPlayerStatsManager().highlightRow(player.getID());

            // Process the player's turn in GUI mode, wait for input
            return processPlayerTurnGUI(player).thenRun(() -> {
                // After player's turn ends, continue to the next step
                player.setActive(false); // Mark player as inactive after turn
            });
        }

        // If it's not GUI mode, return a completed future immediately (CLI logic will handle separately)
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Ends the day for the GUI version of the game.
     */
    private void endDayGUI() {
        // Call the standard end day logic
        endDay();
        
        // Notify GUI-specific observers if needed
        this.model.notifyObservers("SHOW_MESSAGE", "End of Day " + model.getDay() + " in GUI mode.");
    }

    /**
     * Handles the player's actions and determines if the turn ends in GUI mode.
     * 
     * @param player The player whose turn it is.
     */
    private CompletableFuture<Void> processPlayerTurnGUI(Player player) {
        CompletableFuture<Void> turnCompleted = new CompletableFuture<>();
    
        // Start processing the player's actions
        processPlayerActions(player, turnCompleted);
    
        return turnCompleted;
    }

private void processPlayerActions(Player player, CompletableFuture<Void> turnCompleted) {
    this.currentPlayer = player; // Set currentPlayer here
    player.setActive(true);
    player.setHasMoved(false);
    player.setHasWorked(false);
    player.setHasUpgraded(false);
    
    // Create the buttons for the player's available actions
    createPlayerActionButtons(player);

    // Wait for the player's input
    CompletableFuture<Map<String, Object>> playerInputFuture = ((GameGUIView) this.view).getPlayerInputFuture();

    playerInputFuture.thenAccept(commandData -> {
            String command = (String) commandData.get("command");
            String data = (String) commandData.get("data");
            Board board = this.model.getBoard();
            final boolean[] endTurn = {false};
            
            Platform.runLater(() -> {
                // Handle the command with an if-else tree
                if (command.equals("MOVE")) {
                    String locationName = data;
                    // Move player in the model
                    board.setPlayerLocation(player, locationName);
                    player.setHasMoved(true);

                    // Move player on the board
                    Location location = this.model.getLocations()
                                                  .get(locationName);
                    Area area = location.getArea();
                    int x = area.getX();
                    int y = area.getY();
                    // Create a HashMap to hold the event data
                    Map<String, Object> eventData = new HashMap<>();
                    // Add data to eventData
                    eventData.put("playerID", player.getID());
                    eventData.put("locationX", x);
                    eventData.put("locationY", y);
                    // Add the player to the location
                    this.model.notifyObservers("PLAYER_MOVE", eventData);

                } else if (command.equals("WORK")) {
                    String roleName = data;
                    // Set players role in the model
                    // board.setPlayerRole(player.getID(), roleName);
                    player.setHasWorked(true);

                    // // Set player role on the board
                    // // Get the 

                    // // Create a HashMap to hold the event data
                    // Map<String, Object> eventData = new HashMap<>();
                    // // Add data to eventData
                    // eventData.put("playerID", player.getID());
                    // eventData.put("locationX", x);
                    // eventData.put("locationY", y);
                    // // Add the player to the location
                    // this.model.notifyObservers("MOVE_PLAYER", eventData);

                } else if (command.equals("ACT")) {
                    // TODO: add shot counter image to last wrapped Take
                    endTurn[0] = true;
                } else if (command.equals("REHEARSE")) {
                    // Handle rehearse action in model

                    // Handle rehearse action on the board
                    
                    endTurn[0] = true;

                } else if (command.equals("UPGRADE")) {
                    // Handle upgrade action in model
                    // player.upgrade(data); 
                    player.setHasUpgraded(true);

                    // Handle upgrade action on the board

                } else if (command.equals("END")) {
                    // Handle end turn action
                    endTurn[0] = true;
                }

                // Determine if the turn should end based on the command sequence
                if (player.getHasMoved() && (player.getHasWorked() || player.getHasUpgraded())) {
                    endTurn[0] = true;
                }

                // If the player's turn ends, complete the future and end the turn
                if (endTurn[0]) {
                    player.setActive(false);
                    removePlayerActionButtons();
                    // Complete the future to proceed to the next player
                    turnCompleted.complete(null);
                } else {
                    removePlayerActionButtons();
                    // Continue processing the player's actions
                processPlayerActions(player, turnCompleted);
                }
            });
        });
    }

    /**
     * Handles the beginning of each player's turn.
     */
    public void createPlayerActionButtons(Player player) {
        // get player ID
        int currentPlayerID = player.getID();
        // get board
        Board board = this.model.getBoard();
        // get player's string location name
        String locationName = board.getPlayerLocationName(player);

        if (locationName.equals("Trailer")) { // if at Trailer
            if (!player.getHasMoved()) { // if player hasn't moved this turn
                addMoveListenersToNeighbors(player); // Add neighbor location buttons
            }
        } else if(locationName.equals("Casting Office")){ // if at Casting Office
            if (!player.getHasMoved()) { // if player hasn't moved this turn
                // Add neighbor location buttons
                addMoveListenersToNeighbors(player); 
            }
            if (!player.getHasUpgraded()) { // if player hasn't upgraded this turn
                // Add upgrade buttons
                addUpgradeListenerToCastingOffice(player); 
            }
        } else { // if at any other location besides Trailer or Casting Office
            // If player has no Role
            if ((board.getPlayerRole(currentPlayerID) == null)) {
                
                // If the location is not wrapped
                if (!this.model.getLocation(locationName).getIsWrapped()) {
                    addRoleListenersToAvailableRoles(player);
                }

                // if player hasn't moved this turn
                if (!player.getHasMoved()){
                    // Add neighbor location buttons
                    addMoveListenersToNeighbors(player); 
                }
            } else { // if player has a role
                // Add act buttons
                addActListenersToNextTakeAtCurrentLocation(player); 
                // Add rehearse buttons
                addRehearseListenerToCurrentLocationCard(player); 
            }
        }
        // Add End button to dice label
        addEndListenerToPlayerDiceLabel(player); 
    }

    /**
     * Handles the end of each player's turn.
     */
    public void removePlayerActionButtons() {
        this.model.notifyObservers("REMOVE_BUTTONS", null);
    }

    /**
     * Adds mouse listeners to all neighboring locations of the given player.
     *
     * @param player the player to add mouse listeners for
     */
    private void addMoveListenersToNeighbors(Player player) {
        String locationName = this.model.getBoard().getPlayerLocationName(player);
        Location location = this.model.getLocation(locationName);
        List<String> neighbors = location.getNeighbors();

        for (String neighborName : neighbors) {
            // make sure neighborName has first letter capitalized
            String formatedNeighborName = neighborName.substring(0, 1).toUpperCase() + 
                                  neighborName.substring(1);
            Location neighborLocation = 
                this.model.getLocation(formatedNeighborName);
            // Location neighbor = this.model.getLocation(neighborName);
            Area area = neighborLocation.getArea();
            // areasWithListeners.add(area);
            Map<String, Object> eventData = new HashMap<>();
            // Add data to eventData
            eventData.put("command", "MOVE");
            eventData.put("data", neighborName);
            eventData.put("area", area);
            // Add the neighbor location to the view
            this.model.notifyObservers("ADD_BUTTON", eventData);
        }
    }
    
    /**
     * Adds a WORK mouse listeners to all available roles at the player's 
     * current location.
     * 
     * @param player the player to add mouse listeners for
     */
    private void addRoleListenersToAvailableRoles(Player player) {
        // Get location roles
        String locationName = this.model.getBoard().getPlayerLocationName(player);
        Location location = this.model.getLocation(locationName);
        List<Role> roles = location.getRoles();

        // Add roles to the roles list from the scene card
        List<Role> cardRoles = this.model.getBoard()
                                         .getLocationSceneCardRoles(
                                            locationName, 
                                            this.model.getDeck()
                                         );
        roles.addAll(cardRoles);

        // Add role buttons to the view
        for (Role role : roles) {
            // If Player has the rank to assume the role
            if (role.getRank() <= player.getRank()) {
                String roleName = role.getName();
                Area area = role.getArea();
                // Create a HashMap to hold the event data
                Map<String, Object> eventData = new HashMap<>();
                // Add data to eventData
                eventData.put("command", "WORK");
                eventData.put("data", roleName);
                eventData.put("area", area);
                // get the area of the players location
                Area locationArea = location.getArea();
                eventData.put("locationArea", locationArea);


                // Check if the role is on card and add to eventData if true
                if (role.getOnCard()) {
                    eventData.put("onCard", true);
                } else {
                    eventData.put("onCard", false);
                }

                // Add the role to the view
                this.model.notifyObservers("ADD_BUTTON", eventData);
            }
        }
    }

    /**
     * Adds a ACT mouse listener to the first unwrapped Take at the player's 
     * current location.
     * 
     * @param player the player to add mouse listeners for
     */
    private void addActListenersToNextTakeAtCurrentLocation(Player player) {
        // Get the player's location
        String locationName = this.model.getBoard().getPlayerLocationName(player);
        // Get the location
        Location location = this.model.getLocation(locationName);
        // Get the location's takes
        List<Take> takes = location.getTakes();
        // Get the first unwrapped take
        Take nextTake = null;
        for (Take take : takes) {
            if (!take.isWrapped()) {
                nextTake = take;
                break;
            }
        }
        // Get Take area
        Area area = nextTake.getArea();

        // Create a HashMap to hold the event data
        Map<String, Object> eventData = new HashMap<>();
        // Add data to eventData
        eventData.put("command", "ACT");
        eventData.put("data", String.valueOf(nextTake.getNumber()));
        eventData.put("area", area);
        // Add the role to the view
        this.model.notifyObservers("ADD_BUTTON", eventData);
    }

    /**
     * Adds a REHEARSE mouse listener to the area of the player's current 
     * location.
     * 
     * @param player the player to add mouse listeners for
     */
    private void addRehearseListenerToCurrentLocationCard(Player player) {
        // Get the player's location
        String locationName = this.model.getBoard().getPlayerLocationName(player);
        // Get the location
        Location location = this.model.getLocation(locationName);
        // // Get Take area
        Area area = location.getArea();

        // Create a HashMap to hold the event data
        Map<String, Object> eventData = new HashMap<>();
        // Add data to eventData
        eventData.put("command", "REHEARSE");
        eventData.put("data", "Rehearse");
        eventData.put("area", area);
        // Add the role to the view
        this.model.notifyObservers("ADD_BUTTON", eventData);
    }

    /**
     * Adds an END mouse listener to the player die label.
     * 
     * @param player the player
     */
    private void addEndListenerToPlayerDiceLabel(Player player) {
        // Get the player's ID
        String currentPlayerIDString = String.valueOf(player.getID());

        // Create a HashMap to hold the event data
        Map<String, Object> eventData = new HashMap<>();
        // Add data to eventData
        eventData.put("command", "END");
        eventData.put("data", currentPlayerIDString);
        eventData.put("area", new Area(0, 0, 0, 0)); // Empty area

        // Add the player die to the location
        this.model.notifyObservers("ADD_BUTTON", eventData);
    }

    private void addUpgradeListenerToCastingOffice(Player player) {
        // Get player's location name
        String locationName = this.model.getBoard()
                                        .getPlayerLocationName(player);
        // Get the location (must be the Casting Office in this case)
        Location location = this.model.getLocation(locationName);
        // Get the area
        Area area = location.getArea();

        // Create a HashMap to hold the event data
        Map<String, Object> eventData = new HashMap<>();
        // Add data to eventData
        eventData.put("command", "UPGRADE");
        eventData.put("data", "Upgrade");
        eventData.put("area", area); // Empty area

        // Add the player die to the location
        this.model.notifyObservers("ADD_BUTTON", eventData);
    }

    /**
     * Scores the game and displays the final results.
     */
    public void scoreGame() {

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
    public void onMove(String locationName) {
        // Update the menu to show locations the player can select
        int currentPlayerID = this.model.getCurrentPlayer();
        Player currentPlayer = this.model.getPlayer(currentPlayerID);

        // move player to location
        movePlayerToLocation(currentPlayerID, locationName);

        // set player as hasMoved
        currentPlayer.setHasMoved(true);

    }

    /**
     * Handles the Work action.
     */
    public void onWork(String roleName){
        // // Handle move action TODO: Remove line below
        // this.model.notifyObservers(
        //     "SHOW_MESSAGE", 
        //     "Work is Selected"
        // );
        // // Update the menu to show locations the player can select
        // int currentPlayerID = this.model.getCurrentPlayer();
        // Player currentPlayer = this.model.getPlayer(currentPlayerID);
        // String locationString = this.model.getBoard()
        //                                   .getPlayerLocationName(currentPlayer);
        // Location location = this.model.getLocation(locationString);
        // List<Role> locationRoles = location.getRoles();
        // List<Role> cardRoles = this.model.getBoard()
        //                                  .getLocationSceneCardRoles(
        //                                     locationString, 
        //                                     this.model.getDeck()
        //                                  );
        // locationRoles.addAll(cardRoles);
        // // Notify the view to update the menu to the move menu
        // this.model.notifyObservers("UPDATE_WORK_MENU", locationRoles);
    }


    public void onAct(){
        // TODO: finish method
    }

    public void onRehearse(){
        // TODO: finish method
    }

    public void onUpgrade(Map<String, Object> data){
        // TODO: finish method
    }

    /**
     * Handles the End action.
     */
    public void onEnd(){
        // Just don't do anything!
    }


    // public void onSelectNeighbor(String locationString){
    //     // Update the menu to show locations the player can select
    //     int currentPlayerID = this.model.getCurrentPlayer();
    //     // Check if the locationString is "Cancel"

    //     // get location from locationString
    //     Location location = this.model.getLocation(locationString);
    //     // get location area
    //     Area area = location.getArea();
    //     int X = area.getX();
    //     int Y = area.getY();
    //     // Create a HashMap to hold the event data
    //     HashMap<String, Object> eventData = new HashMap<>();
    //     eventData.put("playerID", currentPlayerID);
    //     eventData.put("locationX", X);
    //     eventData.put("locationY", Y);
    //     this.model.notifyObservers("MOVE_TO_LOCATION", eventData);
    // }


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
                    this.model.notifyObservers("ADD_CARD", addCard);
                    // this.model.notifyObservers("ADD_CARD_BACKS", addCardBacks);
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
        this.model.notifyObservers("PLAYER_MOVE", eventData);
    }

}
