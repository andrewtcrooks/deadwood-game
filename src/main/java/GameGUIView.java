import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
// import java.util.concurrent.CountDownLatch;


public class GameGUIView implements GameView {

    private static final String[] diceColor = {
        "r", "o", "y", "g", "c", "b", "v", "p"
    };

    // JavaFX Components
    // Label boardLabel;
    Map<String, Label> playerDiceLabels = new HashMap<>();
    private final Pane bPane;

    // Singleton instance
    private static GameGUIView instance;

    // CountDownLatch
    // private CountDownLatch latch;
    private int numPlayers;

    // Game Action Listener
    private GameActionListener gameActionListener;

    // Managers
    private ButtonManager buttonManager;
    private final ConsoleManager consoleManager;
    // private PlayerStatsManager playerStatsManager;
    private ClickableAreaManager clickableAreaManager;

    private final int BOARD_IMAGE_WIDTH = 1200;
    private final int BOARD_IMAGE_HEIGHT = 900;

    // Constants
    private static final int BOARD_LAYER = 0;
    private static final int SCENE_CARD_LAYER = 1;
    private static final int CONSOLE_LAYER = 1;
    private static final int PSTATS_LAYER = 1;
    private static final int SCENE_CARD_COVER_LAYER = 2;
    private static final int BUTTON_LAYER = 4;
    private static final int PLAYER_DICE_LAYER = 10;
    
    
    private static final int SIDEBAR_LEFT_BORDER = 15;
    private static final int SIDEBAR_RIGHT_BORDER = 8;
    // private static final int MENU_Y = 10;
    private static final int SIDEBAR_WIDTH = 300;
    private static final int BOARD_OFFSET = SIDEBAR_LEFT_BORDER + 
                                            SIDEBAR_WIDTH + 
                                            SIDEBAR_RIGHT_BORDER;

    // private static final int CONSOLE_X = 10;
    private static final int CONSOLE_Y = 16;
    private static final int CONSOLE_WIDTH = 300;
    private static final int CONSOLE_HEIGHT = 464;

    // private static final int PSTATS_X = 10;
    private static final int PSTATS_Y = 497;
    private static final int PSTATS_ROW_HEIGHT = 46;
    private static final int PSTATS_DICE_COLUMN_WIDTH = 46;
    private static final int PSTATS_HEADER_OFFSET = 20;
    private static final int PSTATS_HEADER_HEIGHT = 20;
    private static final int PSTATS_WIDTH = 300;

    private static final int CARD_HEIGHT = 115;
    private static final int CARD_WIDTH = 205;

    private static final int DICE_HEIGHT = 40;
    private static final int DICE_WIDTH = 40;

    private static final int SHOT_HEIGHT = 42;
    private static final int SHOT_WIDTH = 42;

    private Stage stage;


// Constructor


    /**
     * Constructor for the GameGUIView.
     */
    public GameGUIView() {
        super();
        // Initialize the root pane and other components, but not the stage
        this.bPane = new Pane();
        bPane.setStyle("-fx-background-color: #af734a;");

        // Create the deadwood board and boardLabel
        createBoard();
        // Create the player stats manager, and console manager
        this.consoleManager = new ConsoleManager();
        // this.playerStatsManager = new PlayerStatsManager();
        
        // Create the console 
        this.consoleManager.createConsole(
            this.bPane, 
            SIDEBAR_LEFT_BORDER,
            CONSOLE_Y,
            CONSOLE_WIDTH,
            CONSOLE_HEIGHT,
            CONSOLE_LAYER
        );
        // Create the file menu
        createFileMenu();
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

        // Set the title of the window (Stage)
        this.stage.setTitle("Deadwood");

        // Set up the scene and show the stage
        Scene scene = new Scene(
            bPane, 
            BOARD_IMAGE_WIDTH + BOARD_OFFSET, 
            BOARD_IMAGE_HEIGHT
        );
        this.stage.setScene(scene);
    }


// Initialization Methods


    /**
     * Create board.
     */
    private void createBoard() {
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
        boardLabel.setLayoutY(BOARD_LAYER);
        // Add the board label to the board pane
        bPane.getChildren().add(boardLabel);
    }
    
    /**
     * Create the file menu.
     */
    private void createFileMenu() {
        // Create the menu bar
        MenuBar menuBar = new MenuBar();
        // Create the "File" menu
        Menu fileMenu = new Menu("File");
        // Create the "Load" menu item
        MenuItem loadMenuItem = new MenuItem("Load");
        // Add action to the "Load" menu item
        loadMenuItem.setOnAction(e -> showMessage("Load option selected"));
        // Create the "Save" menu item
        MenuItem saveMenuItem = new MenuItem("Save");
        // Add action to the "Save" menu item
        saveMenuItem.setOnAction(e -> showMessage("Save option selected"));
        // Add menu items to the "File" menu
        fileMenu.getItems().addAll(loadMenuItem, saveMenuItem);
        // Add the "File" menu to the menu bar
        menuBar.getMenus().add(fileMenu);
        // Set the menu bar to the stage
        bPane.getChildren().add(menuBar);
    }


// GameView Methods


    /**
     * Get the player's input.
     * 
     * @return The player's input
     */
    @Override
    public String getPlayerInput() {
        // latch = new CountDownLatch(1);
        // try {
        //     latch.await(); // Wait for the button click
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }


        // does nothing in GameGUIView, only for GameCLIView
        return null;
    }

    // /**
    //  * Process the button click.
    //  */
    // public void onButtonClick() {
    //     latch.countDown();
    // }

    /**
     * Get the number of players.
     * 
     * @return The number of players
     */
    public int getNumPlayers() {
        // Create a dialog to get the number of players
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setHeaderText("How many players? (between 2 and 8):");
    
        // Block until valid input is received
        boolean validInput = false;
        while (!validInput) {
            Optional<String> input = inputDialog.showAndWait();
            if (input.isPresent()) {
                try {
                    int numPlayersInput = Integer.parseInt(input.get());
                    if (numPlayersInput >= 2 && numPlayersInput <= 8) {
                        numPlayers = numPlayersInput;
                        validInput = true; // Break out of the loop when input is valid
                    } else {
                        showErrorMessage("Input is not between 2 and 8.");
                        inputDialog.getEditor().clear();
                    }
                } catch (NumberFormatException e) {
                    showErrorMessage("Input is not a valid integer.");
                    inputDialog.getEditor().clear();
                }
            } else {
                // If the user cancels the dialog, exit the application
                Platform.exit();
            }
        }
        
        return numPlayers;
    }

    /**
     * Set the GameActionListener and finish loading the GUI Menu.
     */
    public void setGameActionListener(GameActionListener listener) {
        this.gameActionListener = listener;
        this.buttonManager = new ButtonManager(this.gameActionListener);
        // Create the clickable areas
        this.clickableAreaManager = 
            new ClickableAreaManager(this.buttonManager);
    }


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
                // createPlayerStats(eventData);
                break;
            case "UPDATE_ALL_PLAYER_STATS":
                // updateAllPlayerStats(eventData);
                break;
            case "SHOW_BOARD":
                showBoard();
                break;
            case "ADD_CLICKABLE_AREA":
                @SuppressWarnings("unchecked") 
                Map<String,Object> data = (Map<String,Object>) eventData;
                String command1 = data.get("command").toString();
                String command2 = data.get("data").toString();
                Area area = (Area) data.get("area");
                Area newArea = new Area(
                    area.getX() + BOARD_OFFSET, 
                    area.getY(), 
                    area.getH(), 
                    area.getW()
                );
                clickableAreaManager.createClickableAreas(
                    this.bPane, 
                    command1, 
                    command2, 
                    newArea, 
                    BUTTON_LAYER
                );                
                break;
            case "REMOVE_CLICKABLE_AREAS":
                clickableAreaManager.removeClickableAreas(this.bPane);
                break;
            case "MOVE_TO_LOCATION":
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
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
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

            // Create a new JLabel for the player dice
            Label playerDiceLabel = new Label("Player" + playerID);
            // Create filename reference for dice image
            String diceFilename = diceColor[playerID-1] + rank;
            // Set the icon for the dice label, 0.7f for 70% transparency
            setDiceLabelIcon(playerDiceLabel, diceFilename);
            // Move the dice label bounds to the new location
            movePlayerDieToCoords(x, y, playerDiceLabel);
            
            // Add the player dice label to the playerDiceLabels map
            this.playerDiceLabels.put(playerID.toString(), playerDiceLabel);
            // Add the player dice label to the board
            bPane.getChildren().add(playerDiceLabel);
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
     * Show the board.
     */
    private void showBoard() {
        // Show the board
        this.stage.show();
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
        x += BOARD_OFFSET;
        moveCardToLocation(x, y, cardName, SCENE_CARD_LAYER);
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
        x += BOARD_OFFSET;
        moveCardToLocation(x, y, "CardBack-small.jpg", SCENE_CARD_COVER_LAYER);
    }

    // /**
    //  * Create player stats table.
    //  * 
    //  * @param eventData
    //  */
    // private void createPlayerStats(Object eventData) {
    //     int numPlayers = (int) eventData;
    //     playerStatsManager.createPlayerStatsTable(
    //         bPane, 
    //         numPlayers, 
    //         BOARD_IMAGE_WIDTH, 
    //         PSTATS_ROW_HEIGHT, 
    //         PSTATS_DICE_COLUMN_WIDTH, 
    //         PSTATS_HEADER_OFFSET, 
    //         PSTATS_HEADER_HEIGHT, 
    //         SIDEBAR_LEFT_BORDER, 
    //         PSTATS_Y, 
    //         PSTATS_WIDTH,
    //         PSTATS_LAYER
    //     );
    // }

    // /**
    //  * Update player stats.
    //  * 
    //  * @param eventData
    //  */
    // @SuppressWarnings("unchecked")
    // public void updateAllPlayerStats(Object eventData) {
    //     Map<Integer, List<Integer>> playerData = 
    //         (Map<Integer, List<Integer>>) eventData;
    //     // update player stats
    //     for (Map.Entry<Integer, List<Integer>> entry : playerData.entrySet()) {
    //         Integer playerID = entry.getKey();
    //         List<Integer> playerStats = entry.getValue();
    //         Integer dollars = playerStats.get(0);
    //         Integer credits = playerStats.get(1);
    //         Integer rehearsalTokens = playerStats.get(2);
    //         playerStatsManager.updateStats(
    //             playerDiceLabels, 
    //             playerID, 
    //             dollars, 
    //             credits, 
    //             rehearsalTokens
    //         );        
    //     }

    // }

    /**
     * Handle player move event.
     * 
     * @param eventData The event data
     */
    @SuppressWarnings("unchecked")
    private void handlePlayerMove(Object eventData) {

        // Get the x and y coords of the new location and the player id
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
            Platform.runLater(() -> movePlayerDieToCoords(
                    x + BOARD_OFFSET + 40 * (playerID - 1),
                    y + CARD_HEIGHT,
                    playerLabel
            ));
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
    public void movePlayerDieToCoords(int x, int y, Label playerLabel) {
        Platform.runLater(() -> {
            this.showMessage(
                "Current coords: " + 
                playerLabel.getLayoutX() + 
                ", " + 
                playerLabel.getLayoutY()
            );
            this.showMessage("Moving player die to coords: " + x + ", " + y);
            playerLabel.setLayoutX(x);
            playerLabel.setLayoutY(y);
            this.showMessage(
                "Moved! New coords: " + 
                playerLabel.getLayoutX() + 
                ", " + 
                playerLabel.getLayoutY()
            );
        });
    }
    
    /**
     * Move a card to a location on the board.
     * 
     * @param x The x-coordinate of the location
     * @param y The y-coordinate of the location
     * @param filename The filename of the card
     */
    private void moveCardToLocation(int x, int y, String filename, int layer) {
        // Load the card image
        Image cardImage = new Image(getClass().getClassLoader().getResourceAsStream(filename));
        // Create the card view
        ImageView cardView = new ImageView(cardImage);
        // Set the card view properties
        cardView.setLayoutX(x);
        cardView.setLayoutY(y);
        // Add the card view to the board pane
        bPane.getChildren().add(cardView);
    }

    // /**
    //  * Get the latch.
    //  */
    // public void getLatch() {
    //     latch.countDown();
    // }

    // /**
    //  * Set the latch.
    //  */
    // public void setLatch(CountDownLatch latch) {
    //     this.latch = latch;
    // }

}  