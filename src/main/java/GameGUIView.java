import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Group;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GameGUIView implements GameView {

    private CompletableFuture<String> playerInputFuture;

    // Number of players
    private int numPlayers;

    // Dice colors
    private static final String[] diceColor = {
        "r", "o", "y", "g", "c", "b", "v", "p"
    };

    // Singleton instance
    private static GameGUIView instance;

    // JavaFX Components
    Map<String, Label> playerDiceLabels = new HashMap<>();
    private Group rootGroup;
    private Stage stage;

    // Managers
    private ButtonManager buttonManager;
    private final ConsoleManager consoleManager;
    private PlayerStatsManager playerStatsManager;

    // Constants
    private final int BOARD_IMAGE_WIDTH = 1200;
    private final int BOARD_IMAGE_HEIGHT = 900;
    // private final int BOARD_IMAGE_WIDTH_ADJUST = 28;
    private final int BOARD_IMAGE_HEIGHT_ADJUST = 0; //  54;

    // private static final int BOARD_LAYER = 0;
    // private static final int SCENE_CARD_LAYER = 1;
    // private static final int CONSOLE_LAYER = 1;
    // private static final int PLAYER_STATS_LAYER = 1;
    // private static final int SCENE_CARD_COVER_LAYER = 2;
    // private static final int BUTTON_LAYER = 4;
    // private static final int PLAYER_DICE_LAYER = 10;
    
    private static final int SIDEBAR_LEFT_BORDER = 36;
    private static final int SIDEBAR_RIGHT_BORDER = 28;
    // private static final int MENU_Y = 10;
    private static final int SIDEBAR_WIDTH = 300;
    private static final int BOARD_OFFSET = SIDEBAR_LEFT_BORDER + 
                                            SIDEBAR_WIDTH + 
                                            SIDEBAR_RIGHT_BORDER;

    // private static final int CONSOLE_X = 10;
    private static final int CONSOLE_Y = 8;
    private static final int CONSOLE_WIDTH = 300;
    private static final int CONSOLE_HEIGHT = 464;

    // private static final int PLAYER_STATS_X = 10;
    private static final int PLAYER_STATS_Y = 488;
    private static final int PLAYER_STATS_ROW_HEIGHT = 46;
    private static final int PLAYER_STATS_DICE_COLUMN_WIDTH = 46;
    private static final int PLAYER_STATS_HEADER_OFFSET = 28;
    private static final int PLAYER_STATS_WIDTH = 300;

    private static final int CARD_HEIGHT = 115;
    private static final int CARD_WIDTH = 205;

    private static final int DICE_HEIGHT = 40;
    private static final int DICE_WIDTH = 40;
    private static final int DICE_LOCATION_ROLE_OFFSET_X = 10;
    private static final int DICE_LOCATION_ROLE_OFFSET_Y = 10;
    private static final int DICE_SCENECARD_ROLE_OFFSET_X = 10;
    private static final int DICE_SCENECARD_ROLE_OFFSET_Y = 10;
    private static final int SHOT_HEIGHT = 42;
    private static final int SHOT_WIDTH = 42;


// Constructor


    /**
     * Constructor for the GameGUIView.
     */
    public GameGUIView() {
        // Initialize the root pane and other components, but not the stage
        this.rootGroup = new Group();

        // Add the game board image to the root pane
        createBoard();

        // Create the player stats manager, and console manager
        this.consoleManager = new ConsoleManager();
        this.playerStatsManager = new PlayerStatsManager();
        this.buttonManager = new ButtonManager();
        
        // Create the console 
        this.consoleManager.createConsole(
            this.rootGroup, 
            SIDEBAR_LEFT_BORDER,
            CONSOLE_Y,
            CONSOLE_WIDTH,
            CONSOLE_HEIGHT
        );
    }


// Singleton Pattern


    /**
     * Get the singleton instance of the GameGUIView.
     * @return the singleton instance
     */
    public static GameGUIView getInstance() {
        if (instance == null) {
            instance = new GameGUIView();
        }
        return instance;
    }

    /**
     * Set the stage after the singleton is created.
     * 
     * @param primaryStage the primary stage
     */
    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
        
        // If no scene is set, create and set a new one
        Scene scene = new Scene(rootGroup, BOARD_IMAGE_WIDTH + BOARD_OFFSET, BOARD_IMAGE_HEIGHT + BOARD_IMAGE_HEIGHT_ADJUST);
        
        // Set the background color for the scene
        scene.setFill(javafx.scene.paint.Color.web("#AF734A"));
        
        // Set the scene to the stage
        this.stage.setScene(scene);
    }


// Initialization Methods


    /**
     * Create board.
     */
    public void createBoard() {
        // Create the board label
        Label boardLabel = new Label();
        // Load the board image
        InputStream boardImageStream = getClass().getClassLoader().getResourceAsStream("board.jpg");
        if (boardImageStream == null) {
            System.err.println("Error: board.jpg not found");
            return;
        }
        Image boardImage = new Image(boardImageStream);
        // Set the board view to the board image
        ImageView boardView = new ImageView(boardImage);
        // Set the board view to the board graphic
        boardLabel.setGraphic(boardView);
        // Set the board label properties
        boardLabel.setLayoutX(BOARD_OFFSET);
        boardLabel.setLayoutY(0);
        // Add the board label to the board pane
        rootGroup.getChildren().add(boardLabel);
    }
    

// GameView Methods


    /**
     * Get the number of players.
     * 
     * @return The number of players
     */
    public int getNumPlayers() {
        // vestigial, use getNumPlayersFuture instead
        return 0;
    }

    /**
     * Get the number of players asynchronously.
     * 
     * @return A CompletableFuture with the number of players
     */
    public CompletableFuture<Integer> getNumPlayersFuture() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        promptForNumPlayers(future);
        return future;
    }

    /**
     * Recursively prompt the user for the number of players.
     * 
     * @param future The future to complete with the number of players
     */
    private void promptForNumPlayers(CompletableFuture<Integer> future) {
        // Use JavaFX dialog to get input from the user
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deadwood");
        dialog.setHeaderText("Enter the number of players (2-8):");

        Optional<String> response = dialog.showAndWait();
        if (response.isPresent()) {
            try {
                int numPlayers = Integer.parseInt(response.get());
                if (numPlayers >= 2 && numPlayers <= 8) {
                    future.complete(numPlayers);
                } else {
                    promptForNumPlayers(future); // Recursive call to prompt again
                }
            } catch (NumberFormatException e) {
                promptForNumPlayers(future); // Recursive call to prompt again
            }
        } else {
            Platform.exit(); // Exit the application if the dialog is canceled
        }
    }

    // /**
    //  * Set the GameActionListener and finish loading the GUI Menu.
    //  */
    // public void setGameActionListener(GameActionListener listener) {
    //     this.gameActionListener = listener;
    //     this.buttonManager = new ButtonManager();
    // }


// Observer Pattern


    /**
     * Updates the view.
     * 
     * @param eventType The type of event
     * @param eventData The event data
     */
    @Override
    public void update(String eventType, Object eventData) {
        switch (eventType) {
            case "SHOW_MESSAGE":
                showMessage((String) eventData);
                break;
            case "INIT_DICE_LABELS":
                showMessage("Welcome to Deadwood!");
                initDiceLabels(eventData);
                break;
            case "ADD_CARD":
                addCard(eventData);
                break;
            case "ADD_CARD_BACKS":
                addCardBack(eventData);
                break;
            case "CREATE_PLAYER_STATS_TABLE":
                createPlayerStats();
                break;
            case "UPDATE_ALL_PLAYER_STATS":
                updateAllPlayerStats(eventData);
                break;
            case "HIGHLIGHT_PLAYER_ROW":
                playerStatsManager.highlightRow((int) eventData);
                break;
            case "ADD_BUTTON":
                addButton(eventData);
                break;
            case "REMOVE_BUTTONS":
                buttonManager.removeClickableAreas(this.rootGroup);
                break;
            case "PLAYER_MOVE":
                handlePlayerMove(eventData);
                break;
            case "LOAD_GAME":
                handleLoadGame(eventData);
                break;
            case "SCORE_UPDATE":
                handleScoreUpdate(eventData);
                break;
            case "GAME_EVENT":
                handleGameEvent(eventData);
                break;
            default:
                System.out.println("Unknown event type: " + eventType);
        }
    }


// Utility Methods


    /**
     * Show the board.
     */
    public void showBoard() {
        // Show the board
        this.stage.show();
    }

    /**
     * Refresh the board.
     */
    public void refreshView() {
        // Refresh board elements
        rootGroup.requestLayout();
    }

    /**
     * Get the player stats manager.
     * 
     * @return The player stats manager
     */
    public PlayerStatsManager getPlayerStatsManager() {
        return this.playerStatsManager;
    }

// Observer Pattern Methods


    /**
     * Display a message.
     * 
     * @param message The message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Show an error message.
     * 
     * @param message The error message to display
     */
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Create player dice labels.
     * 
     * @param eventData number of players (int)
     */
    @SuppressWarnings("unchecked")
    private void initDiceLabels(Object eventData) {
        // Cast eventData to Map<Integer, Map<String, Integer>>
        Map<Integer, Map<String, Integer>> playerData = 
            (Map<Integer, Map<String, Integer>>) eventData;
        // numPlayers is the size of the playerData map
        int numPlayers = playerData.size();
        // Determine initial dice rank based on number of players
        int rank = numPlayers < 7 ? 1 : 2; // 1 if numPlayers under 7, else 2
    
        // Create player dice labels and store them in playerDiceLabels
        for (Map.Entry<Integer, Map<String, Integer>> entry : 
            playerData.entrySet()) {
            Integer playerID = entry.getKey();
            Map<String, Integer> playerInfo = entry.getValue();
            int x = playerInfo.get("locationX") + BOARD_OFFSET;
            int y = playerInfo.get("locationY");
    
            


            // Create a new Label for the dice image
            Label diceImageLabel = new Label();
            // Create filename reference for dice image
            String diceFilename = diceColor[playerID - 1] + rank;
            // Set the icon for the dice label (dice image)
            setDiceLabelIcon(diceImageLabel, diceFilename);
            
            // Create a new Label for the player dice text (this will be hidden initially)
            Label diceTextLabel = new Label("Player " + playerID);
            diceTextLabel.setVisible(false); // Hide the text initially
    
            // Position the text label next to or above/below the dice image
            diceTextLabel.setLayoutX(x);  // Adjust as needed for positioning
            diceTextLabel.setLayoutY(y - 20);  // Slightly above the dice image

            // // Add hover effect on the dice image to show/hide the text
            // diceImageLabel.setOnMouseEntered(e -> diceTextLabel.setVisible(true));
            // diceImageLabel.setOnMouseExited(e -> diceTextLabel.setVisible(false));
    
            // Add the dice image and the text label to the board
            rootGroup.getChildren().addAll(diceImageLabel, diceTextLabel);
    
            // Move the dice label bounds to the new location
            movePlayerDieToCoords(diceImageLabel, x, y);

            // Store the dice image label in the map
            this.playerDiceLabels.put(playerID.toString(), diceImageLabel);
        }
    }

    /**
     * Set the icon for the dice label.
     * 
     * @param playerDiceLabel The player dice label
     * @param diceFilename The filename of the dice image
     */
    private void setDiceLabelIcon(Label playerDiceLabel, String diceFilename) {
        InputStream diceImageStream = getClass().getClassLoader().getResourceAsStream("dice/" + diceFilename + ".png");
        if (diceImageStream != null) {
            Image diceImage = new Image(diceImageStream);
            playerDiceLabel.setGraphic(new ImageView(diceImage));
        } else {
            System.err.println("Error loading dice image: " + diceFilename);
        }
    }



    /**
     * Add a card to the GUI board.
     * 
     * @param eventData The event data
     */
    @SuppressWarnings("unchecked")
    public void addCard(Object eventData) {
        Map<String, Integer> addCard = (Map<String, Integer>) eventData;
        int cardID = addCard.get("sceneCardID");
        String cardName = String.format("cards/%02d.png", cardID);
        int x = addCard.get("locationX");
        int y = addCard.get("locationY");
        // Add offset to x to compensate for left sidebar
        int new_x = x + BOARD_OFFSET;
        moveCardToLocation(new_x, y, cardName);
    }

    /**
     * Add card backs to GUI board.
     * 
     * @param eventData The event data
     */
    @SuppressWarnings("unchecked")
    private void addCardBack(Object eventData) {
        // Add card backs to GUI board
        Map<String, Integer> addCardBack = (Map<String, Integer>) eventData;
        int x = addCardBack.get("locationX");
        int y = addCardBack.get("locationY");
        // Add offset to x to compensate for left sidebar
        int new_x = x + BOARD_OFFSET;
        moveCardToLocation(new_x, y, "CardBack-small.jpg");
    }

    /**
     * Create player stats table.
     * 
     */
    private void createPlayerStats() {
        playerStatsManager.createPlayerStatsTable(
            rootGroup, 
            playerDiceLabels,
            PLAYER_STATS_ROW_HEIGHT,
            PLAYER_STATS_DICE_COLUMN_WIDTH,
            PLAYER_STATS_HEADER_OFFSET,
            SIDEBAR_LEFT_BORDER,
            PLAYER_STATS_Y,
            PLAYER_STATS_WIDTH
        );
    }

    /**
     * Update player stats.
     * 
     * @param eventData The event data
     */
    @SuppressWarnings("unchecked")
    public void updateAllPlayerStats(Object eventData) {
        Map<Integer, List<Integer>> playerData = 
            (Map<Integer, List<Integer>>) eventData;
        // update player stats
        for (Map.Entry<Integer, List<Integer>> entry : playerData.entrySet()) {
            Integer playerID = entry.getKey();
            List<Integer> playerStats = entry.getValue();
            Integer dollars = playerStats.get(0);
            Integer credits = playerStats.get(1);
            Integer rehearsalTokens = playerStats.get(2);
            playerStatsManager.updatePlayerStat(
                playerID, 
                dollars, 
                credits, 
                rehearsalTokens
            );
        }
    }

    /**
     * Add a clickable area to the GUI board.
     * 
     * @param eventData The event data
     */
    @SuppressWarnings("unchecked")    
    public void addButton(Object eventData) {
        Map<String,Object> data = (Map<String,Object>) eventData;
        String command = data.get("command").toString();
        String info = data.get("data").toString();
        Area area = (Area) data.get("area");
        String tooltipText = 
            command.substring(0, 1).toUpperCase() +
            command.substring(1).toLowerCase();

        Area offsetArea = new Area(
            area.getX() + BOARD_OFFSET, 
            area.getY(), 
            area.getH(), 
            area.getW()
        );

        // if ( command == "UPGRADE"){
        //     // TODO: finish this part since its special buttons
        //     int stophere = 1;

        // } else 
        
        if ( command.equals("END") ){
            // Get the button area from the playerDiceLabel
            Label diceLabel = this.playerDiceLabels.get(info);
            Area endArea = new Area(
                (int) diceLabel.getLayoutX() - 4, 
                (int) diceLabel.getLayoutY() - 4, 
                (int) diceLabel.getHeight() + 8, 
                (int) diceLabel.getWidth() + 8
            );

            buttonManager.createButton(
                this.rootGroup, 
                command, 
                info, 
                endArea,
                tooltipText
            ); 
        } else if ( command.equals("WORK") ){ // Coomand was WORK

            boolean onCard = (boolean) data.get("onCard");
            Area locationArea = (Area) data.get("locationArea");

            if (onCard){ // Role is OnCard and needs location 
                // If onCard the role area needs to have the location x and y 
                // added onti it's own x and y


                // Get the scene card for the role from the board
                // board.getLocation


                Area workArea = new Area(
                    offsetArea.getX() + locationArea.getX() - 5, 
                    offsetArea.getY() + locationArea.getY() - 4, 
                    offsetArea.getH() + 9, 
                    offsetArea.getW() + 10
                );
                buttonManager.createButton(
                        this.rootGroup, 
                        command, 
                        info, 
                        workArea,
                        tooltipText
                );
            } else { // Role is off Card and needs location correction
                Area workArea = new Area(
                    offsetArea.getX() - 3, 
                    offsetArea.getY() - 3, 
                    offsetArea.getH() + 6, 
                    offsetArea.getW() + 6
                );
                buttonManager.createButton(
                    this.rootGroup, 
                    command, 
                    info, 
                    workArea,
                    tooltipText
                );
            }

        } else{ // Command was MOVE, ACT, or REHEARSE

            buttonManager.createButton(
                this.rootGroup, 
                command, 
                info, 
                offsetArea,
                tooltipText
            ); 

            int stophere = 1;
        }
        
    }
    
    /**
     * Handle player move event.
     * 
     * @param eventData The event data
     */
    @SuppressWarnings("unchecked")
    private void handlePlayerMove(Object eventData) {

        // Get the x and y coords of the new location and the player id
        String command = (String) ((Map<String, Object>) eventData).get("command");
        int x = (int) ((Map<String, Object>) eventData).get("locationX");
        int y = (int) ((Map<String, Object>) eventData).get("locationY");
        int playerID = 
            (int) ((Map<String, Object>) eventData).get("playerID");
        
        // Get the player label
        String key = String.valueOf(playerID);
        Label playerLabel = 
            playerDiceLabels.get(key);

        // Update the dice label location for the player
        if (playerLabel != null) {
            if (command.equals("MOVE")) {

                int adjustedY = y + CARD_HEIGHT;
                if (playerID >= 5 && playerID <= 8) {
                    adjustedY -= CARD_HEIGHT;
                }
                movePlayerDieToCoords(
                    playerLabel,
                    x + BOARD_OFFSET + 40 * ((playerID - 1) % 4),
                    adjustedY
                );
            } else if (command.equals("WORK")) {
                movePlayerDieToCoords(
                    playerLabel,
                    x + BOARD_OFFSET,
                    y
                );
            } else if(command.equals("Trailer")){

            } else if (command.equals("Casting Office")){

            }

        }

    }

    private void handleLoadGame(Object eventData) {
        // TODO: Handle load game event
    }

    private void handleScoreUpdate(Object eventData) {
        // Handle score update event
        System.out.println("Score updated: " + eventData);
    }

    private void handleGameEvent(Object eventData) {
        // Handle general game event
        System.out.println("Game event occurred: " + eventData);
    }


//Observer Pattern Method Utilities


    /**
     * Move a die to a location on the board.
     * 
     * @param x The x-coordinate of the location
     * @param y The y-coordinate of the location
     * @param playerLabel The dice label of the player
     */
    public void movePlayerDieToCoords(Label playerLabel, int x, int y) {
        playerLabel.setLayoutX(x);
        playerLabel.setLayoutY(y);
    }
    
    /**
     * Move a card to a location on the board.
     * 
     * @param x The x-coordinate of the location
     * @param y The y-coordinate of the location
     * @param filename The filename of the card
     */
    private void moveCardToLocation(int x, int y, String filename) {
        // Load the card image
        Image cardImage = new Image(getClass().getClassLoader().getResourceAsStream(filename));
        // Create the card view
        ImageView cardView = new ImageView(cardImage);
        // Set the card view properties
        cardView.setLayoutX(x);
        cardView.setLayoutY(y);
        // Add the card view to the board pane
        rootGroup.getChildren().add(cardView);
    }

    /**
     * Get the player's input in GUI mode.
     * 
     * @return The player's input
     */
    public String getPlayerInput() {
        // Not used in GUI mode
        return null;
    }

    // /**
    //  * Get the player's input in CLI mode.
    //  * 
    //  * @return The player's input
    //  */
    // @Override
    // public String getPlayerInput() {
    //     if (playerInputFuture == null || playerInputFuture.isDone()) {
    //         playerInputFuture = CompletableFuture.supplyAsync(() -> {
    //             buttonManager.waitForClick();
    //             return buttonManager.getClickedArea();
    //         });
    //     }

    //     try {
    //         return playerInputFuture.get();
    //     } catch (InterruptedException | ExecutionException e) {
    //         Thread.currentThread().interrupt();
    //         throw new RuntimeException(e);
    //     }
    // }

    public CompletableFuture<Map<String, Object>> getPlayerInputFuture() {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        
        // Get the clicked button's command and data when a button is clicked
        buttonManager.setOnButtonClick((command, data) -> {
            Map<String, Object> commandData = new HashMap<>();
            commandData.put("command", command);
            commandData.put("data", data);
            future.complete(commandData); // Complete the future with the clicked button's info
        });
    
        return future;
    }


    /**
     * Interface for the number of players callback.
     */
    @FunctionalInterface
    public interface NumPlayersCallback {
        void onNumPlayersReceived(int numPlayers);
    }
}  