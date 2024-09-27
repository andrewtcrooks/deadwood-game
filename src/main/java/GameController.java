import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Collections;
import javafx.application.Platform;


/**
 * Represents the game controller for the game.
 * It manages the game flow.
 */


public class GameController{
    private static final boolean debug = false;
    private GameModel model;
    private GameView view;
    private int nextPlayerIndex = 0;
    private boolean dayEnded = false;
    private static final HashMap<String, PlayerAction> actionMap = 
        new HashMap<>();
    private List<Player> playerTurnOrder;
    private Set<String> visitedLocations = new HashSet<>();
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
        if (view instanceof GameGUIView) {
            this.playerTurnOrder = new ArrayList<>(model.getPlayers());
        }

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
        dayEnded = true;

        if (debug){ // debug
            this.model.notifyObservers("SHOW_MESSAGE", "End of day triggered.");
        }
        
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
            // Prevents location roles from staying occupied on the next day
            resetAllLocationRolesToUnoccupied();
            // reset all takes
            this.model.getLocations().values().forEach(location -> {
                location.getTakes().forEach(take -> {
                    take.reset();
                });
            });

            // remove all remaining buttons
            this.model.notifyObservers("REMOVE_ALL_BUTTONS", null);
            // Remove all shots from the board
            this.model.notifyObservers("REMOVE_ALL_SHOTS", null);
            // bring all player dice to front
            this.model.notifyObservers("BRING_DICE_TO_FRONT", null);

            // Reset visited locations
            visitedLocations.clear();
        }
    }

    /**
     * Scores the game and displays the final results.
     */
    public void scoreGame() {


        // Print game over screen
        this.model.notifyObservers(
            "SHOW_MESSAGE", 
            "Game over!"
        );

        // Get the list of players from the model
        List<Player> players = this.model.getPlayers();

        // Create a copy of the players list
        List<Player> playersCopy = new ArrayList<>(players);

        // Sort players list copy by score and then by ID if scores are tied
        playersCopy.sort(
            Comparator.comparingInt(Player::getScore)
                    .reversed()
                    .thenComparingInt(player -> player.getID())
        );

        // Get highest score
        int highestScore = playersCopy.get(0).getScore();

        // Get the winners
        List<Player> winners = new ArrayList<>();
        for (Player player : players) {
            if (player.getScore() == highestScore) {
                winners.add(player);
            } else {
                break;
            }
        }

        // Print scores and indicate the winner(s)
        List<String> playerScoreStringList = new ArrayList<>();
        for (Player player : players) {
            String winnerIndicator = player.getScore() == highestScore ? "           !!!Winner!!!" : "";
            String playerScoreString = " : " + player.getScore() + winnerIndicator;
            playerScoreStringList.add(playerScoreString);
        }
        this.model.notifyObservers("SHOW_SCORES", playerScoreStringList);
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
    
        // Base case: if the current day is greater than the total number of days, stop recursion.
        if (day > numDays) {
            this.model.notifyObservers("SHOW_MESSAGE", "All days completed.");
            return CompletableFuture.completedFuture(null);  // Game ends
        }
        
        // Notify the beginning of the day
        this.model.notifyObservers("SHOW_MESSAGE", "Day " + day + " of " + numDays + " has begun.");

        if (debug) { //debug
            this.model.notifyObservers("SHOW_MESSAGE", "At start of Day " + day + ", nextPlayerIndex is: " + nextPlayerIndex);
        }

        // Play the current day
        return playDayGUI().thenCompose(ignored -> {

            // Increment day in the model
            this.model.incrementDay();
            
            // Recursively call playDaysGUI() to handle the next day
            return playDaysGUI();
        });

    }

    /**
     * Manages a single day in the game, allowing multiple cycles of player turns.
     * 
     * @return CompletableFuture that completes when the day ends.
     */
    private CompletableFuture<Void> playDayGUI() {
        if (debug) {
            this.model.notifyObservers("SHOW_MESSAGE", "Starting playDayGUI. nextPlayerIndex is: " + nextPlayerIndex);
        }
        // Reset the flag for the new day
        dayEnded = false;  

        // Start processing player cycles
        return processPlayerCycle().thenCompose(ignored -> {
            // Check if the day has ended
            if (dayEnded) {
                // Day has ended, notify and return
                if (debug){ // debug
                    this.model.notifyObservers("SHOW_MESSAGE", "Day " + this.model.getDay() + " has ended.");
                    this.model.notifyObservers("SHOW_MESSAGE", "At end of Day, nextPlayerIndex is: " + nextPlayerIndex);
                }

                // Check if this is not the last day
                if (this.model.getDay() < this.model.getNumDays()) {
                    endDay();  // Only call endDay() if it's not the last day
                    if (debug){ // debug
                        this.model.notifyObservers("SHOW_MESSAGE", "endDay() called.");
                    }
                } else {
                    if (debug){ // debug
                        this.model.notifyObservers("SHOW_MESSAGE", "Last day has ended. Skipping endDay().");
                    }
                }

                return CompletableFuture.completedFuture(null);
            } else {
                // Day has not ended, continue with the next player cycle
                return playDayGUI();
            }
        });
    }

    /**
     * Processes a full cycle of all players' turns.
     * 
     * @return CompletableFuture that completes when the cycle is done.
     */
    private CompletableFuture<Void> processPlayerCycle() {
        if (dayEnded) {
            // Day has ended, stop processing cycles
            return CompletableFuture.completedFuture(null);
        }
        
        if (debug){
            this.model.notifyObservers("SHOW_MESSAGE", "Starting a new player cycle.");
        }

            // Start processing the list of players from the first player
        return processPlayersRecursive(playerTurnOrder.iterator()).thenCompose(ignored -> {
            // Check if the day has ended
            if (dayEnded) {
                // Day has ended, stop processing further cycles
                return CompletableFuture.completedFuture(null);
            } else {
                // Day has not ended, continue with the next player cycle
                return processPlayerCycle();
            }
        });
    }

    private CompletableFuture<Void> processPlayersRecursive(Iterator<Player> playerIterator) {
        if (dayEnded || !playerIterator.hasNext()) {
            // Completed a full cycle of all players
            if (debug){
                this.model.notifyObservers("SHOW_MESSAGE", "Completed a full cycle of player turns.");
            }
            return CompletableFuture.completedFuture(null);
        }
    
        Player player = playerIterator.next();
    
        if (debug){
            this.model.notifyObservers("SHOW_MESSAGE", "Processing turn for Player ID: " + player.getID());
        }
    
        return playerTurnGUI(player.getID()).thenCompose(ignored -> {
            // Check if the number of scenes is less than or equal to 1
            if (this.model.getBoard().getNumScenesRemaining() <= 1) {
                // Day has ended, set the flag and stop processing further players
                dayEnded = true;
                // Rotate the playerTurnOrder so that the next player is at the front
                rotatePlayerTurnOrder(player);
                if (debug) { // debug
                    this.model.notifyObservers("SHOW_MESSAGE", "Day has ended. Next player is: Player ID " + playerTurnOrder.get(0).getID());
                }
                return CompletableFuture.completedFuture(null);
            } else {
                // Day has not ended, continue with the next player's turn
                return processPlayersRecursive(playerIterator);
            }
        });
    }

    private void rotatePlayerTurnOrder(Player lastPlayer) {
        int index = playerTurnOrder.indexOf(lastPlayer);
        if (index >= 0) {
            Collections.rotate(playerTurnOrder, - (index + 1));
        }
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

        // Highlight the player's row in the player stats table
        this.model.notifyObservers("HIGHLIGHT_PLAYER_ROW", player.getID());

        // Process the player's turn in GUI mode, wait for input
        return processPlayerTurnGUI(player).thenCompose(ignored -> {
            
            // Mark player as inactive after turn
            player.setActive(false);  

            // Player's turn ends
            return CompletableFuture.completedFuture(null);
        });
    }

    /**
     * Handles the player's actions and determines if the turn ends in GUI mode.
     * 
     * @param player The player whose turn it is.
     */
    private CompletableFuture<Void> processPlayerTurnGUI(Player player) {

        // Create a CompletableFuture to signal when the player's turn is done
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
        player.setActive(true);
    
        
        if (debug){ // debug
            this.model.notifyObservers("SHOW_MESSAGE", "processPlayerActions: Starting actions for Player ID: " + player.getID());
        }
        
        // Create the buttons for the player's available actions
        createPlayerActionButtons(player);

        // Wait for the player's input
        CompletableFuture<Map<String, Object>> playerInputFuture = ((GameGUIView) this.view).getPlayerInputFuture();

        playerInputFuture.thenAccept(commandData -> {
            String command = (String) commandData.get("command");
            Object data = commandData.get("data");
            
            Platform.runLater(() -> {
                final boolean[] endTurn = {false};
                try {
                    // Handle the command with an if-else tree
                    switch (command) {
                        case "MOVE":
                            handleMoveCommand(player, data);
                            break;
                        case "WORK":
                            handleWorkCommand(player, data);
                            break;
                        case "ACT":
                            handleActCommand(player);
                            endTurn[0] = true;
                            break;
                        case "REHEARSE":
                            handleRehearseCommand(player);
                            endTurn[0] = true;
                            break;
                        case "UPGRADE":
                            handleUpgradeCommand(player, data);
                            break;
                        case "END":
                            handleEndCommand(player);
                            endTurn[0] = true;
                            break;
                        default:
                            this.model.notifyObservers("SHOW_MESSAGE", "Unknown command: " + command);
                    }
    
                    // After player's action ends, remove buttons
                    removePlayerActionButtons();
    
                    // After player's action ends, check if only 1 scene remains
                    if (this.model.getBoard().getNumScenesRemaining() <= 1) {
                        // End the day if only 1 scene remains
                        dayEnded = true;
                        // player.setActive(false);

                        if (debug){ // debug
                            this.model.notifyObservers("SHOW_MESSAGE", "Day incremented to: " + this.model.getDay());
                        }
                        turnCompleted.complete(null);
                        return;
                    }
    

                    // Determine if the turn should end based on the command sequence
                    if (player.getHasMoved() && (player.getHasWorked() || player.getHasUpgraded())) {
                        endTurn[0] = true;
                    }
    
                    // If the player's turn ends, complete the future and end the turn
                    if (endTurn[0]) {
                        player.setActive(false);
    
                        // Complete the future to proceed to the next player
                        turnCompleted.complete(null);
                    } else {
                        if (debug){ // debug
                            this.model.notifyObservers("SHOW_MESSAGE", "Continuing actions for Player ID: " + player.getID());
                        }
                        

                        // Recursively call processPlayerActions to continue the turn
                        processPlayerActions(player, turnCompleted);
                    }
                } catch (Exception ex) {
                        ex.printStackTrace();
                        turnCompleted.completeExceptionally(ex);
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
        this.model.notifyObservers("REMOVE_ALL_BUTTONS", null);
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
        // Retrieve the list of upgrades
        List<Upgrade> upgrades = this.model.getUpgrades();

        // Filter the upgrades based on the player's rank and resources
        List<Upgrade> availableUpgrades = upgrades.stream()
            .filter(upgrade -> upgrade.getLevel() > player.getRank())
            .filter(upgrade -> {
                if (upgrade.getCurrency().equals("dollar")) {
                    return player.getDollars() >= upgrade.getAmt();
                } else if (upgrade.getCurrency().equals("credit")) {
                    return player.getCredits() >= upgrade.getAmt();
                }
                return false;
            })
            .collect(Collectors.toList());

        // Create a HashMap to hold the event data
        Map<String, Object> eventData = new HashMap<>();
        // Add data to eventData
        eventData.put("command", "UPGRADE");
        eventData.put("data", String.valueOf(player.getID()));
        eventData.put("area", new Area(0, 0, 0, 0)); // Empty area
        eventData.put("availableUpgrades", availableUpgrades); // Add available upgrades

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

            // Add player location to playerInfo
            playerInfo.put("locationX", x);
            playerInfo.put("locationY", y);
            initDiceLabels.put(playerID, playerInfo);
        }

        // Initialize the player dice in the view
        this.model.notifyObservers("INIT_DICE_LABELS", initDiceLabels);



        // move all players to trailer using observer method for "PLAYER_MOVE"
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
                    addCard.put("sceneCardID", sceneCardID);
                    addCard.put("locationX", x);
                    addCard.put("locationY", y);
                    this.model.notifyObservers("ADD_CARD", addCard);

                    // Make a Map to hold the coords for the card back
                    Map<String, Object> addCardBack = new HashMap<>();
                    addCardBack.put("locationName", location.getName());
                    addCardBack.put("locationX", x);
                    addCardBack.put("locationY", y);
                    this.model.notifyObservers("ADD_CARD_BACK", addCardBack);
                }
            }
        }
    }

    /**
     * Resets all location roles to unoccupied.
     */
    public void resetAllLocationRolesToUnoccupied() {
        // get location
        Map<String,Location> locations = this.model.getLocations();
        // for each location
        for (Location location : locations.values()) {
            List<Role> roles = location.getRoles();
            for (Role role : roles) {
                role.setOccupied(false);
            }
        }
    }

    /**
     * Handles the MOVE command.
     */
    private void handleMoveCommand(Player player, Object data) {
        String locationName = (String) data;
        
        // Move player in the model
        this.model.getBoard().setPlayerLocation(player, locationName);
        player.setHasMoved(true);

        // Check if the location has been visited yet on this day
        if (!visitedLocations.contains(locationName) && 
            !locationName.equals("Trailer") && 
            !locationName.equals("Casting Office")
        ) {
            visitedLocations.add(locationName);

            // Remove the card back for this location
            Map<String, Object> removeCardBack = new HashMap<>();
            removeCardBack.put("locationName", locationName);
            this.model.notifyObservers("REMOVE_CARD_BACK", removeCardBack);
        }

        // Move player on the board
        Location location = this.model.getLocations().get(locationName);
        Area area = location.getArea();

        // Create a HashMap to hold the event data
        Map<String, Object> eventData = new HashMap<>();

        // Add data to eventData
        eventData.put("locationName", locationName);
        eventData.put("playerID", player.getID());
        eventData.put("locationArea", area);

        // Add the player to the location
        this.model.notifyObservers("PLAYER_MOVE", eventData);
    }


    /**
     * Handles the work command by moving the player to the selected role.
     * 
     * @param player The player to move.
     * @param data The role name to move to.
     */
    private void handleWorkCommand(Player player, Object data) {
        String roleName = (String) data;
        // Get the current player's location
        Location location = this.model.getLocation(this.model.getBoard().getPlayerLocationName(player));

        // Get all the location and scene card roles
        List<Role> locationRoles = location.getRoles();
        List<Role> sceneCardRoles = this.model.getBoard().getLocationSceneCardRoles(location.getName(), this.model.getDeck());

        // Determine if the roleName is onCard
        boolean onCard = sceneCardRoles.stream().anyMatch(role -> role.getName().equals(roleName));

        int x = 0;
        int y = 0;

        // If on card, add the player to the x and y coordinates of the location plus the role area
        if (onCard) {
            // Get the x and y coords for the role on the scene card
            for (Role role : sceneCardRoles) {
                if (role.getName().equals(roleName)) {
                    x = role.getArea().getX() + location.getArea().getX() + 1;
                    y = role.getArea().getY() + location.getArea().getY() + 1;
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
        eventData.put("playerID", player.getID());
        eventData.put("locationName", location.getName());
        eventData.put("locationArea", new Area(x, y, 0, 0));
        // Add the player to the location
        this.model.notifyObservers("PLAYER_WORK", eventData);

        this.model.getBoard().setPlayerRole(player.getID(), roleName);

        // Set player as hasWorked
        player.setHasWorked(true);
    }

    /**
     * Handles the ACT command.
     */
    private void handleActCommand(Player player) {
        // Handle act action in model
        actionMap.get("act").execute(player, model, view);

        // End turn
        // No additional logic needed here as endTurn flag is set in processPlayerActions
    }

    /**
     * Handles the REHEARSE command.
     */
    private void handleRehearseCommand(Player player) {
        // Handle rehearse action in model
        actionMap.get("rehearse").execute(player, model, view);

        // End turn
        // No additional logic needed here as endTurn flag is set in processPlayerActions
    }

    /**
     * Handles the UPGRADE command.
     */
    private void handleUpgradeCommand(Player player, Object data) {
        Upgrade upgrade = (Upgrade) data;
        // Handle upgrade action in model
        PlayerActionUpgrade upgradeAction = (PlayerActionUpgrade) actionMap.get("upgrade");
        upgradeAction.processPayment(player, upgrade.getLevel(), upgrade.getCurrency());
        upgradeAction.upgradePlayerRank(player, upgrade.getLevel(), this.view);
        player.setHasUpgraded(true);
    }

    /**
     * Handles the END command.
     */
    private void handleEndCommand(Player player) {
        // Handle end turn action
        actionMap.get("end").execute(player, model, view);
        // End turn
        // No additional logic needed here as endTurn flag is set in processPlayerActions
    }
}
