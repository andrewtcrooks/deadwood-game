import java.util.*;

/**
 * This class represents the game controller for the game.
 * It manages the game flow.
 */
public class GameController{
    private GameModel model;
    private GameView view;
    private int currentPlayer;
    private static final HashMap<String, PlayerAction> actionMap = new HashMap<>();
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

    /**
     * Constructs a new GameController.
     */
    public GameController () {
        this.model = null;
        this.view = null;
    }

    /**
     * Initializes the game with the necessary settings, including setting up the model and view,
     * and getting the number of players to initialize in the model.
     * 
     * @param model The game model to be used.
     * @param view The game view to be used.
     * @param boardXMLFilePath The file path to the board XML file.
     * @param cardsXMLFilePath The file path to the cards XML file.
     */
    public void initializeGame(GameModel model, GameView view, String boardXMLFilePath, String cardsXMLFilePath) {
        // Initialize current player, player actions, model, and view
        this.currentPlayer = 1; // Player 1 starts the game
        this.model = model;
        this.view = view;
        
        // Get the number of players and initialize the model
        boolean initializationSuccessful = false;
        while (!initializationSuccessful) {
            try {
                int numPlayers = this.view.getNumPlayers();
                this.model.initModel(numPlayers, boardXMLFilePath, cardsXMLFilePath);
                initializationSuccessful = true; // If this line is reached, no exception was thrown
            } catch (IllegalArgumentException e) {
                this.view.showMessage("Invalid number of players: " + e.getMessage());
                // Loop will continue until a valid number of players is provided
            }
        }
    }

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
            // increment day by one
            this.model.incrementDay();
        }
    }

    /**
     * Manages a player's turn in the game.
     * 
     * @param ID The ID of the player whose turn it is.
     */
    public void playerTurn(int ID) {
        boolean endTurn = false;
        Player player = this.model.getPlayer(ID);
        player.setActive(true);
        player.setHasMoved(false);
        player.setHasWorked(false);
        player.setHasUpgraded(false);

        while (!endTurn) {
            // Get the player's input
            String command = this.view.getPlayerInput();
            // Get the action associated with the command
            PlayerAction action = actionMap.get(command);
            // Continue if input command not recognized
            if (action == null) {
                this.view.showMessage("Command not recognized. Please try again.");
                continue;
            }
            // Validate and potentially execute the action
            if (action.validate(player, model, view)) {
                endTurn = action.execute(player, model, view); // If execute returns true, the turn ends
            }
        }
        // Set player to inactive
        player.setActive(false);
    }

    /**
     * Scores the game and displays the final results.
     */
    public void scoreGame() {
        // Get the players
        List<Player> players = model.getPlayers();
        // Sort players by score and then by ID if scores are tied
        players.sort(Comparator.comparingInt(Player::getScore).reversed().thenComparingInt(player -> player.getID()));
    
        // Print game over screen
        view.showMessage("Game over!");

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
            String winnerIndicator = player.getScore() == highestScore ? "  !Winner!" : "";
            System.out.println("Player ID: " + player.getID() + ", Score: " + player.getScore() + winnerIndicator);
        }
    }

    /**
     * Manages a day in the game.
     */
    private void playDay() {
        // Display the beginning of the day message
        view.showMessage("Day " + (model.getDay()) + " has begun.");
        // cycle through each player's turn until only 1 scene is left
        while (this.model.getBoard().getNumScenesRemaining() > 1) {
            // manage the player's turn
            playerTurn(this.currentPlayer);
            // player starts at 1, move to the next player 2 through 8, then 1 through 8 repeatedly
            this.currentPlayer = (this.currentPlayer % this.model.getNumPlayers()) + 1;
        }
    }

    /**
     * Ends the day by resetting the board for the next day.
     */
    private void endDay() {
        // reset the board for the next day
        model.getBoard().resetBoard();
    }

}
