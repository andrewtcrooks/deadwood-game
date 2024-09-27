import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.ColorAdjust;
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

    // Dice colors
    private static final String[] diceColor = {
        "red", "orange", "yellow", "green", "cyan", "blue", "violet", "pink"
    };

    // Singleton instance
    private static GameGUIView instance;

    // JavaFX Components
    Map<String, Label> playerDiceLabels = new HashMap<>();
    private Group rootGroup;
    private Stage stage;
    private Map<String, ImageView> cardBacks = new HashMap<>();

    // Managers
    private final ConsoleManager consoleManager;
    private PlayerStatsManager playerStatsManager;
    private ButtonManager buttonManager;
    private ShotManager shotManager;

    // Constants
    private final int BOARD_IMAGE_WIDTH = 1200;
    private final int BOARD_IMAGE_HEIGHT = 900;
    private final int BOARD_IMAGE_HEIGHT_ADJUST = 0;
    
    private static final int SIDEBAR_LEFT_BORDER = 36;
    private static final int SIDEBAR_RIGHT_BORDER = 28;
    private static final int SIDEBAR_WIDTH = 300;
    private static final int BOARD_OFFSET_X = SIDEBAR_LEFT_BORDER + 
                                            SIDEBAR_WIDTH + 
                                            SIDEBAR_RIGHT_BORDER;

    private static final int CONSOLE_Y = 8;
    private static final int CONSOLE_WIDTH = 300;
    private static final int CONSOLE_HEIGHT = 464;

    private static final int PLAYER_STATS_Y = 488;
    private static final int PLAYER_STATS_ROW_HEIGHT = 46;
    private static final int PLAYER_STATS_DICE_COLUMN_WIDTH = 46;
    private static final int PLAYER_STATS_HEADER_OFFSET = 28;
    private static final int PLAYER_STATS_WIDTH = 300;

    private static final int CARD_HEIGHT = 115;

    private static final int TRAILER_X_OFFSET = 11;
    private static final int TRAILER_Y_OFFSET = 104;
    private static final int CASTING_OFFICE_X_OFFSET = 14;
    private static final int CASTING_OFFICE_Y_OFFSET = -15;
    private static final int DICE_X_SPACING = 46;
    private static final int DICE_Y_SPACING = 46;

    private static final int UPGRADE_BUTTON_X_OFFSET = -8;
    private static final int UPGRADE_BUTTON_Y_OFFSET = -2;
    private static final int UPGRADE_BUTTON_Y_SPACING = 0;
    private static final int UPGRADE_BUTTON_H = 21;
    private static final int UPGRADE_BUTTON_W = 36;


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
        this.playerStatsManager = new PlayerStatsManager(diceColor);
        this.buttonManager = new ButtonManager();
        this.shotManager = new ShotManager(BOARD_OFFSET_X);
        
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
        Scene scene = new Scene(rootGroup, BOARD_IMAGE_WIDTH + BOARD_OFFSET_X, BOARD_IMAGE_HEIGHT + BOARD_IMAGE_HEIGHT_ADJUST);
        
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
        boardLabel.setLayoutX(BOARD_OFFSET_X);
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


// Observer Pattern


    /**
     * Updates the view.
     * 
     * @param eventType The type of event
     * @param eventData The event data
     */
    @SuppressWarnings("unchecked")
    @Override
    public void update(String eventType, Object eventData) {
        switch (eventType) {
            case "SHOW_MESSAGE":
                showMessage((String) eventData);
                break;
            case "INIT_DICE_LABELS":
                showMessage("Welcome to Deadwood!");
                initDiceLabels((Map<Integer, Map<String, Integer>>) eventData);
                break;
            case "ADD_CARD":
                addCard((Map<String, Integer>) eventData);
                break;
            case "ADD_CARD_BACK":
                addCardBack((Map<String, Object>) eventData, false);
                break;
            case "ADD_WRAPPED_CARD_BACK":
                addCardBack((Map<String, Object>) eventData, true);
                break;
            case "REMOVE_CARD_BACK":
                removeCardBack((Map<String, Object>) eventData);
                break;
            case "CREATE_PLAYER_STATS_TABLE":
                List<Player> players = (List<Player>) eventData;
                addDiceImageListeners(players);
                createPlayerStats(players);
                break;
            case "HIGHLIGHT_PLAYER_ROW":
                playerStatsManager.highlightRow((int) eventData);
                break;
            case "ADD_BUTTON":
                addButton((Map<String,Object>) eventData);
                break;
            case "REMOVE_ALL_BUTTONS":
                buttonManager.clearAllButtons(this.rootGroup);
                break;
            case "REMOVE_ALL_SHOTS":
                shotManager.clearShotCounters(this.rootGroup);
                break;
            case "BRING_DICE_TO_FRONT":
                bringPlayerDiceToFront();
                break;
            case "PLAYER_MOVE":
                handlePlayerMove((Map<String, Object>) eventData);
                break;
            case "PLAYER_WORK":
                handlePlayerWork((Map<String, Object>) eventData);
                break;
            case "SHOW_SCORES":
                showScores((List<String>) eventData);
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
     * Brings all the dice labels to the front of the board.
     * This is necessary because the dice labels may be covered
     * by other nodes in the board, such as the player cards.
     */
    public void bringPlayerDiceToFront() {
        for (Label label : playerDiceLabels.values()) {
            label.toFront();
        }
    }

    /**
     * Get the shot manager for the board.
     * 
     * @return The shot manager
     */
    public ShotManager getShotManager() {
        return this.shotManager;
    }

    /**
     * Get the button manager for the board.
     * 
     * @return The button manager
     */
    public ButtonManager getButtonManager() {
        return this.buttonManager;
    }

    /**
     * Get the player stats manager.
     * 
     * @return The player stats manager
     */
    public PlayerStatsManager getPlayerStatsManager() {
        return this.playerStatsManager;
    }

    /**
     * Get the root group for the board.
     * 
     * @return The root group
     */
    public Group getGroup() {
        return this.rootGroup;
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
     * Create player dice labels.
     * 
     * @param eventData number of players (int)
     */
    private void initDiceLabels(Map<Integer, Map<String, Integer>> playerData) {
    
        // Create player dice labels and store them in playerDiceLabels
        for (Map.Entry<Integer, Map<String, Integer>> entry : 
            playerData.entrySet()) {
            Integer playerID = entry.getKey();
            Map<String, Integer> playerInfo = entry.getValue();
            int x = playerInfo.get("locationX") + BOARD_OFFSET_X;
            int y = playerInfo.get("locationY");
            int playerRank = playerInfo.get("playerRank");
    
            // Create a new Label for the dice image
            Label diceImageLabel = new Label();
            // Create filename reference for dice image
            String diceFilename = diceColor[playerID - 1].substring(0, 1) + playerRank;
            // Set the icon for the dice label (dice image)
            setDiceLabelIcon(diceImageLabel, diceFilename);
            
            // Create a new Label for the player dice text (this will be hidden initially)
            Label diceTextLabel = new Label("Player " + playerID);
            diceTextLabel.setVisible(false); // Hide the text initially
    
            // Position the text label next to or above/below the dice image
            diceTextLabel.setLayoutX(x);  // Adjust as needed for positioning
            diceTextLabel.setLayoutY(y - 20);  // Slightly above the dice image
    
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
     * Add a card to board.
     * 
     * @param cardInfo The card info
     */
    public void addCard(Map<String, Integer> cardInfo) {
        int cardID = cardInfo.get("sceneCardID");
        String cardName = String.format("cards/%02d.png", cardID);
        int x = cardInfo.get("locationX");
        int y = cardInfo.get("locationY");
        // Add offset to x to compensate for left sidebar
        int new_x = x + BOARD_OFFSET_X;
        moveCardToLocation(new_x, y, cardName);
    }

    /**
     * Add card backs to board.
     * 
     * @param eventData The event data containing location details.
     */
    private void addCardBack(Map<String, Object> addCardBack, boolean isGrayscale) {
        String locationName = (String) addCardBack.get("locationName");
        int x = (Integer) addCardBack.get("locationX");
        int y = (Integer) addCardBack.get("locationY");

        // Add offset to x to compensate for left sidebar
        int new_x = x + BOARD_OFFSET_X;

        // Load the card back image
        InputStream cardBackImageStream = getClass().getClassLoader().getResourceAsStream("CardBack-small.jpg");
        if (cardBackImageStream == null) {
            System.err.println("Error: CardBack-small.jpg not found");
            return;
        }
        Image cardBackImage = new Image(cardBackImageStream);

        // Create an ImageView for the card back
        ImageView cardBackView = new ImageView(cardBackImage);
        cardBackView.setLayoutX(new_x);
        cardBackView.setLayoutY(y);

        // Apply grayscale effect if required
        if (isGrayscale) {
            applyWrappedSceneEffect(cardBackView);
        }

        // Add the card back to the root group
        rootGroup.getChildren().add(cardBackView);

        // Store the card back in the map if not grayscale
        if (!isGrayscale) {
            cardBacks.put(locationName, cardBackView);
        }

    }

    /**
     * Apply a dark gray effect to the given ImageView to indicate a wrapped scene.
     * 
     * @param imageView The ImageView to apply the effect to.
     */
    private void applyWrappedSceneEffect(ImageView imageView) {
        ColorAdjust darkGrayAdjust = new ColorAdjust();

        // Shift hue to neutral (no color)
        darkGrayAdjust.setHue(0);          
          
        // Desaturate to remove original colors
        darkGrayAdjust.setSaturation(-1);

        // Darkens the image
        darkGrayAdjust.setBrightness(-0.7);

        // Adds more depth
        darkGrayAdjust.setContrast(0.4);
        
        // Apply the effect to the image view
        imageView.setEffect(darkGrayAdjust);
    }
    
    /**
     * Remove card backs from board.
     * 
     * @param eventData The event data containing location details.
     */
    private void removeCardBack(Map<String, Object> removeCardBack) {
        String locationName = (String) removeCardBack.get("locationName");
        ImageView cardBackView = cardBacks.get(locationName);

        if (cardBackView != null) {
            // Remove the card back from the root group
            rootGroup.getChildren().remove(cardBackView);
            // Remove the entry from the map
            cardBacks.remove(locationName);
        } else {
            System.err.println("Error: No card back found for location " + locationName);
        }
    }

    /**
     * Create player stats table.
     * 
     */
    private void createPlayerStats(List<Player> players) {
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

        // Add the player data to the table
        playerStatsManager.addPlayerData(players);
    }

    /**
     * Adds listeners to update dice labels when rank changes.
     * 
     * @param players The list of players to add listeners for.
     */
    public void addDiceImageListeners(List<Player> players){
        // Add listeners to update dice labels when rank changes
        for (Player player : players) {
            Label diceLabel = playerDiceLabels.get(String.valueOf(player.getID()));
            if (diceLabel != null) {
                player.rankProperty().addListener((obs, oldRank, newRank) -> {
                    Platform.runLater(() -> {
                        String newDiceFilename = diceColor[player.getID() - 1].substring(0, 1) + newRank.intValue();
                        setDiceLabelIcon(diceLabel, newDiceFilename);
                    });
                });
            }
        }
    }

    /**
     * Add a clickable area to the GUI board.
     * 
     * @param eventData The event data
     */
    public void addButton(Map<String,Object> buttonData) {
        String command = buttonData.get("command").toString();
        String info = buttonData.get("data").toString();
        Area area = (Area) buttonData.get("area");
        String tooltipText = 
            command.substring(0, 1).toUpperCase() +
            command.substring(1).toLowerCase();

        Area offsetArea = new Area(
            area.getX() + BOARD_OFFSET_X, 
            area.getY(), 
            area.getH(), 
            area.getW()
        );
        
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

            boolean onCard = (boolean) buttonData.get("onCard");
            Area locationArea = (Area) buttonData.get("locationArea");

            if (onCard){ // Role is OnCard and needs location 

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

        } else if ( command.equals("UPGRADE") ){
            @SuppressWarnings("unchecked")
            List<Upgrade> availableUpgrades = 
                (List<Upgrade>) buttonData.get("availableUpgrades");

            // Create a button for each upgrade
            for (Upgrade upgrade : availableUpgrades) {
                int rank = upgrade.getLevel();
                Area upgradeArea = upgrade.getArea();
                Area upgradeOffsetArea = new Area(
                    upgradeArea.getX() + UPGRADE_BUTTON_X_OFFSET 
                                       + BOARD_OFFSET_X, 
                    upgradeArea.getY() + UPGRADE_BUTTON_Y_OFFSET +
                                         (rank-2) * UPGRADE_BUTTON_Y_SPACING, 
                    UPGRADE_BUTTON_H, 
                    UPGRADE_BUTTON_W
                );
                buttonManager.createButton(
                    this.rootGroup, 
                    command, 
                    upgrade, 
                    upgradeOffsetArea,
                    tooltipText
                );
            }


        } else { // Command was MOVE, ACT, or REHEARSE

            buttonManager.createButton(
                this.rootGroup, 
                command, 
                info, 
                offsetArea,
                tooltipText
            ); 
        }
        
    }
    
    /**
     * Handle player move event.
     * 
     * @param eventData The event data
     */
    private void handlePlayerMove(Map<String, Object> moveData) {

        // Get the x and y coords of the new location and the player id
        String locationName = (String) moveData.get("locationName");
        Area area  = (Area) moveData.get("locationArea");
        int playerID = (Integer) moveData.get("playerID");
        
        int x = area.getX();
        int y = area.getY();

        // Get the player label
        String key = String.valueOf(playerID);
        Label playerLabel = 
            playerDiceLabels.get(key);

        // Update the dice label location for the player
        if (playerLabel != null) {
            if(locationName.equals("Trailer")){
                int adjustedY = y + TRAILER_Y_OFFSET;
                if (playerID >= 5 && playerID <= 8) {
                    adjustedY += DICE_Y_SPACING;
                }
                movePlayerDieToCoords(
                    playerLabel,
                    x + 
                        BOARD_OFFSET_X + 
                        TRAILER_X_OFFSET + 
                        DICE_X_SPACING * ((playerID - 1) % 4),
                    adjustedY
                );
            } else if (locationName.equals("Casting Office")){
                int adjustedY = y + DICE_Y_SPACING + CASTING_OFFICE_Y_OFFSET;
                if (playerID >= 5 && playerID <= 8) {
                    adjustedY -= DICE_Y_SPACING;
                }
                movePlayerDieToCoords(
                    playerLabel,
                    x + 
                        BOARD_OFFSET_X + 
                        CASTING_OFFICE_X_OFFSET + 
                        DICE_X_SPACING * ((playerID - 1) % 4),
                    adjustedY
                );
            } else {
                int adjustedY = y + CARD_HEIGHT;
                if (playerID >= 5 && playerID <= 8) {
                    adjustedY -= CARD_HEIGHT;
                }
                movePlayerDieToCoords(
                    playerLabel,
                    x + BOARD_OFFSET_X + 40 * ((playerID - 1) % 4),
                    adjustedY
                );
            }
        }

    }


    /**
     * Handle player work event.
     * 
     * @param eventData The event data
     */
    private void handlePlayerWork(Map<String, Object> moveData) {

        // Get the x and y coords of the new location and the player id
        Area area  = (Area) moveData.get("locationArea");
        int playerID = (Integer) moveData.get("playerID");
        
        int x = area.getX();
        int y = area.getY();

        // Get the player label
        String key = String.valueOf(playerID);
        Label playerLabel = 
            playerDiceLabels.get(key);

        // Update the dice label location for the player
        if (playerLabel != null) {
            // Move the player dice to the new location
            movePlayerDieToCoords(
                playerLabel,
                x + BOARD_OFFSET_X,
                y
            );
        }

    }

    /**
     * Display the final scores for all players.
     * 
     * @param playerScores A list of strings each containing the final score for a player.
     */
    private void showScores(List<String> playerScores) {
        int index = 0;
        for (String playerScoreString : playerScores) {
            String color = diceColor[index];
            String colorFormatted = color.substring(0, 1)
                                         .toUpperCase() + 
                                    color.substring(1);
            String colorPlayerScoreString = colorFormatted + playerScoreString;
            // messageArea.append(coloredMessage + "\n");
            showMessage(colorPlayerScoreString);
            index += 1;
        }
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

    public CompletableFuture<Map<String, Object>> getPlayerInputFuture() {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        
        // Get the clicked button's command and data when a button is clicked
        buttonManager.setOnButtonClick((command, data) -> {
            Map<String, Object> commandData = new HashMap<>();
            commandData.put("command", command);
            commandData.put("data", data);
            // Complete the future with the clicked button's info
            future.complete(commandData); 
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