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
    // Command Pattern action maps
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


// ============================================================
// Constructor and Initialization
// ============================================================


    /**
     * Constructs a new GameController.
     */
    public GameController () {
        this.model = null;
        this.view = null;
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
    }
    

// ============================================================
// Shared Methods
// ============================================================


    /**
     * Ends the day by resetting the board for the next day.
     */
    private void endDay() {
        // TODO: remove debug line below
        this.model.notifyObservers("SHOW_MESSAGE", "End of day triggered.");

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

        if (view instanceof GameGUIView) {
            //TODO: fix this part that clear up the gui each day

            // Get the area for the Trailer location from the model
            Area trailerArea = this.model.getLocation("Trailer").getArea();

            // Get all players from the model
            List<Player> players= this.model.getPlayers();

            // Return all players to Trailer
            for (Player player : players) {
                // get player ID
                int playerID = player.getID();
                // Create a HashMap to hold the event data
                Map<String, Object> eventData = new HashMap<>();
                // Add data to eventData
                eventData.put("locationName", "Trailer");
                eventData.put("locationArea", trailerArea);
                eventData.put("playerID", playerID);

                // Add the player die to the location
                this.model.notifyObservers("PLAYER_MOVE", eventData);

            }

            // Redeal the cards and card backs
            initializeLocationCards();

            // remove all remaining buttons
            this.model.notifyObservers("REMOVE_ALL_BUTTONS", null);
            // Remove all shots from the board
            this.model.notifyObservers("REMOVE_ALL_SHOTS", null);
        }
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

    
// ============================================================
// CLI-Specific Methods
// ============================================================

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


// ============================================================
// GUI-Specific Methods
// ============================================================


    /**
     * Initializes the board elements, including the location scene cards, the
     * player dice, and the player stats table, for the
     * GUI. This method is only called in GUI mode.
     */
    public void createBoardElements() {
        // Initialize player dice, stats, etc., for the GUI
        initializeLocationCards();
        initializePlayerDice();
        this.model.notifyObservers(
            "CREATE_PLAYER_STATS_TABLE", 
            model.getPlayers()
        );                
    }

    /**
     * Manages the days in the game asynchronously for the GUI version.
     * 
     * @return CompletableFuture that completes when all days are done.
     */
    public CompletableFuture<Void> playDaysGUI() {
        int numDays = this.model.getNumDays();
        int day = this.model.getDay();

        // TODO: remove debug line below
        this.model.notifyObservers("SHOW_MESSAGE", "Current Day: " + day + " / Total Days: " + numDays);

        if (day > numDays) {
            // TODO: remove debug line below
            this.model.notifyObservers("SHOW_MESSAGE", "All days completed. Ending game.");
            // Complete the game when all days are done
            return CompletableFuture.completedFuture(null);
        }

        // Display the beginning of the day message
        this.model.notifyObservers("SHOW_MESSAGE", "Day " + model.getDay() + " has begun.");

        return playDayGUI().thenCompose(ignored -> {
            // After the day ends, increment the day and move to the next
            this.model.incrementDay();

            // TODO: remove debug line below
            this.model.notifyObservers("SHOW_MESSAGE", "Day incremented to: " + this.model.getDay());

            // Recursively play the next day
            return playDaysGUI();
        });
    }

    /**
     * Manages a day in the game asynchronously for the GUI version.
     * 
     * @return CompletableFuture that completes when the day is done.
     */
    private CompletableFuture<Void> playDayGUI() {
        // Process the turns for all players during this day in GUI mode
        return processTurnsForAllPlayersGUI().thenCompose(ignored -> {
            // Check if more than 1 scene remains to continue the day
            if (this.model.getBoard().getNumScenesRemaining() > 1) {
                // Continue processing turns until only 1 scene remains
                return playDayGUI();
            } else {
                // If fewer than 1 scene remains, end the day
                endDay();
                // Day is complete
                return CompletableFuture.completedFuture(null);
            }
        });
    }

    /**
     * Processes the turns for all players sequentially in GUI mode using CompletableFutures.
     * 
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

        if (view instanceof GameGUIView) {
            // Highlight the player's row in the player stats table
            this.model.notifyObservers("HIGHLIGHT_PLAYER_ROW", player.getID());

            // Process the player's turn in GUI mode, wait for input
            return processPlayerTurnGUI(player).thenCompose(ignored -> {
                // After player's turn ends, check if only 1 scene remains
                if (this.model.getBoard().getNumScenesRemaining() == 1) {
                    endDay();  // End the day if only 1 scene remains
                    return CompletableFuture.completedFuture(null);
                } else {
                    player.setActive(false);  // Mark player as inactive after turn
                    return CompletableFuture.completedFuture(null);
                }
            });
        }

        // If it's not GUI mode, return a completed future immediately (CLI logic will handle separately)
        return CompletableFuture.completedFuture(null);
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

    /**
     * Handles the player's actions and determines if the turn ends in GUI mode.
     * 
     * @param player The player whose turn it is.
     * @param turnCompleted The CompletableFuture that completes when the turn ends.
     */
    private void processPlayerActions(Player player, CompletableFuture<Void> turnCompleted) {
        this.currentPlayer = player; // Set currentPlayer here
        player.setActive(true);
    
        // Remove all existing buttons before adding new ones
        removePlayerActionButtons();
    
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
                    // Create a HashMap to hold the event data
                    Map<String, Object> eventData = new HashMap<>();
                    // Add data to eventData
                    eventData.put("locationName", locationName);
                    eventData.put("playerID", player.getID());
                    eventData.put("locationArea", area);

                    // Add the player to the location
                    this.model.notifyObservers("PLAYER_MOVE", eventData);

                } else if (command.equals("WORK")) {
                    String roleName = data;
                    // Get the current player's location
                    // int currentPlayerID = this.model.getCurrentPlayer();
                    Location location = this.model.getLocation(
                        board.getPlayerLocationName(currentPlayer)
                    );

                    // Get all the location and scene card roles
                    List<Role> locationRoles = location.getRoles();
                    List<Role> sceneCardRoles = board.getLocationSceneCardRoles(
                        location.getName(), this.model.getDeck()
                    );

                    // Determine if the roleName is onCard
                    boolean onCard = false;
                    for (Role role : sceneCardRoles) {
                        if (role.getName().equals(roleName)) {
                            onCard = true;
                            break;
                        }
                    }

                    int x = 0;
                    int y = 0;

                    
                    // if on card add the player to the x and y coordinates of the location 
                    // plus the role area
                    if (onCard) {
                        // get the x and y coords for the role on the scene card
                        for (Role role : sceneCardRoles) {
                            if (role.getName().equals(roleName)) {
                                x = role.getArea().getX() + 
                                    location.getArea().getX() +
                                    1;
                                y = role.getArea().getY() + 
                                    location.getArea().getY() +
                                    1;
                                role.setOccupied(true);
                                break;
                            }
                        }
                    } else {
                        for (Role role : locationRoles) {
                            if (role.getName().equals(roleName)) {
                                x = role.getArea().getX() + 3;
                                y = role.getArea().getY() + 3;
                                role.setOccupied(true);
                                break;
                            }
                        }
                    }
                    // Create a HashMap to hold the event data
                    Map<String, Object> eventData = new HashMap<>();
                    // Add data to eventData
                    eventData.put("playerID", currentPlayer.getID());
                    eventData.put("locationName", location.getName());
                    eventData.put("locationArea", new Area(x, y, 0, 0));
                    // Add the player to the location
                    this.model.notifyObservers("PLAYER_WORK", eventData);
                    
                    board.setPlayerRole(currentPlayer.getID(), roleName);

                    // set player as hasWorked
                    currentPlayer.setHasWorked(true);
                    
                } else if (command.equals("ACT")) {
                    // TODO: add shot counter image to last wrapped Take

                    // Handle act action in model
                    actionMap.get("act").execute(player, model, view);

                    // TODO: Update location shot counter images

                    // end turn
                    endTurn[0] = true;
                } else if (command.equals("REHEARSE")) {
                    // Handle rehearse action in model
                    actionMap.get("rehearse").execute(player, model, view);

                    // end turn
                    endTurn[0] = true;

                } else if (command.equals("UPGRADE")) {
                    // Handle upgrade action in model
                    // actionMap.get("upgrade").execute(player, model, view);
                    // player.upgrade(data); 
                    player.setHasUpgraded(true);

                } else if (command.equals("END")) {
                    // Handle end turn action
                    actionMap.get("end").execute(player, model, view);
                    // end turn
                    endTurn[0] = true;
                }

                // Determine if the turn should end based on the command sequence
                if (player.getHasMoved() && (player.getHasWorked() || player.getHasUpgraded())) {
                    endTurn[0] = true;
                }

                // If the player's turn ends, complete the future and end the turn
                if (endTurn[0]) {
                    player.setActive(false);
                    // removePlayerActionButtons();
                    // Complete the future to proceed to the next player
                    turnCompleted.complete(null);
                } else {
                    // removePlayerActionButtons();
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
                // addUpgradeListenerToCastingOffice(player); 
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
        this.model.notifyObservers("REMOVE_ALL_BUTTONS", null);
    }

    /**
     * Adds mouse listeners to all neighboring locations of the given player.
     *
     * @param player the player to add mouse listeners for
     */
    private void addMoveListenersToNeighbors(Player player) {
        String locationName = this.model.getBoard().getPlayerLocationName(player);
        // String formattedLocationName = locationName.substring(0, 1).toUpperCase() + 
        //                                locationName.substring(1);
        Location location = this.model.getLocation(locationName);
        List<String> neighbors = location.getNeighbors();

        for (String neighborName : neighbors) {
            // make sure neighborName has first letter capitalized
            // String formattedNeighborName = neighborName.substring(0, 1).toUpperCase() + 
            //                       neighborName.substring(1);
            Location neighborLocation = this.model.getLocation(neighborName);
            Area area = neighborLocation.getArea();
            // Create a HashMap to hold the event data
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

        // Create a new list for roles to avoid compounding
        List<Role> roles = new ArrayList<>();

        // Add location roles to the new list
        roles.addAll(location.getRoles());

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
            if (role.getRank() <= player.getRank() && !role.isOccupied()) {
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
            if (!take.isWrapped() && 
                (nextTake == null || take.getNumber() < nextTake.getNumber())
            ) {
                nextTake = take;
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


// ============================================================
// Observer Pattern Methods
// ============================================================


    /**
     * Initializes the player dice in the view with the correct placement.
     * For the Trailer and Casting Office locations, the player dice are placed
     * in a 2x4 grid. For other locations, the player dice are stacked first
     * below the card for players 1-4 and along top of card for players 5-8.
     */
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

            // if (
            //     locationString.equals("Trailer") || 
            //     locationString.equals("Casting Office")
            // ) {
            //     // Calculate row and column for Trailer and Casting Office
            //     int row = (playerID - 1) / 4;
            //     int col = (playerID - 1) % 4;
            //     x += col * TRAILER_CASTING_X_OFFSET;
            //     y += row * TRAILER_CASTING_Y_OFFSET;
            // } else {
            //     // Apply general Y offset for other locations
            //     y += playerID * GENERAL_Y_OFFSET;
            // }
            // Add player location to playerInfo
            playerInfo.put("locationX", x);
            playerInfo.put("locationY", y);
            initDiceLabels.put(playerID, playerInfo);
        }

        // Initialize the player dice in the view
        this.model.notifyObservers("INIT_DICE_LABELS", initDiceLabels);



        // move all playres to trailer using obsever method for "PLAYER_MOVE"
        // get Trailer location
        Location trailer = this.model.getLocation("Trailer");
        Area area = trailer.getArea();

        for (Player player : this.model.getPlayers()) {
            // create moveData
            Map<String, Object> moveData = new HashMap<>();
            moveData.put("playerID", player.getID());
            moveData.put("locationName", "Trailer");
            moveData.put("locationArea", area);
            this.model.notifyObservers("PLAYER_MOVE", moveData);
        }
    }

    /**
     * Initializes the location cards on the board.
     * Adds the scene cards and card backs to the board.
     */
    public void initializeLocationCards(){
        Board board = this.model.getBoard();

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
                    // Make a Map to hold card ID and coords for the scene card
                    Map<String, Integer> addCard = new HashMap<>();
                    // Make a Map to hold the coords for the card back
                    Map<String, Integer> addCardBacks = new HashMap<>();
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
        eventData.put("locationName", locationName);
        eventData.put("locationArea", location.getArea());
        
        // Pass the string and the movePlayer HashMap to notifyObservers
        this.model.notifyObservers("PLAYER_MOVE", eventData);
    }

}
