import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



/*
 * Deadwood by Andrew Crooks
 *  
 * This is the main class for the Deadwood application.
 * 
 * It creates the model, view, and controller for the game.
 * It also initializes the controller and runs the application.
 */
public class Deadwood extends Application{

    private static final Properties config = new Properties();
    private static boolean isCLIMode = false;

    /**
     * The main method for Deadwood.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        loadConfiguration();

        // Check for command-line arguments
        if (args.length > 0) {
            switch (args[0]) {
                case "--cli":
                    isCLIMode = true;
                    startCLI();
                    break;

                case "--help":
                    // Display the help menu and exit
                    displayHelpMenu();
                    System.exit(0);

                default:
                    System.out.println("Unknown option: " + args[0]);
                    displayHelpMenu();
                    System.exit(1);
            }
        } else {
        
            // Default to GUI mode if no --cli flag is provided
            launch(args); // Launch JavaFX for GUI
        }
    }

    /**
     * Load the configuration file.
     */
    private static void loadConfiguration() {
        try (InputStream input = Deadwood.class.getClassLoader()
                                                .getResourceAsStream(
                                                    "config.properties"
                                                )
        ) {
            if (input == null) {
                throw new IOException("Unable to find config.properties");
            }
            // Load the config.properties file
            config.load(input);
        } catch (IOException e) {
            System.out.println(
                "Error loading config.properties: " + 
                e.getMessage()
            );
        }
    }

    /**
     * Display the help menu.
     */
    private static void displayHelpMenu() {
        System.out.println("Usage: java Deadwood [OPTION]");
        System.out.println("Options:");
        System.out.println("  --cli      Run the game in Command Line " + 
                           "Interface (CLI) mode.");
        System.out.println("  --help     Display this help menu.");
        System.out.println("\nIf no option is provided, the game will run in" + 
                           " GUI mode by default.");
    }


// CLI Mode


    /**
     * Starts the Deadwood application in CLI mode.
     */
    private static void startCLI() {
        try {
            GameView view = initializeCLIView();
            GameModel model = initializeModel(view);
            GameController controller = initializeCLIController(model, view);
            playGame(controller);
        } catch (Exception e) {
            System.err.println("An error occurred during CLI initialization: " + 
                               e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the CLI view.
     * 
     * @return the initialized view
     */
    private static GameView initializeCLIView() {
        GameView view = GameCLIView.getInstance();
        view.showMessage("Welcome to Deadwood (CLI Mode). Let's play!");
        return view;
    }

    /**
     * Initializes the game model for both CLI and GUI views.
     * 
     * @param view
     * @return the initialized model
     */
    private static GameModel initializeModel(GameView view) {
        GameModel model = GameModel.getInstance();
        model.registerObserver(view);
        return model;
    }

    /**
     * Initializes the CLI controller
     * 
     * @param model 
     * @param view 
     * @return the initialized controller
     */
    private static GameController initializeCLIController(
        GameModel model, 
        GameView view
    ) {
        GameController controller = new GameController();
        controller.initializeGame(
            model, 
            view, 
            config.getProperty("boardXMLFilePath"), 
            config.getProperty("cardsXMLFilePath")
        );
        return controller;
    }

    /**
     * Plays the game in CLI mode.
     * @param controller
     */
    private static void playGame(GameController controller) {
        controller.playDays();
        controller.scoreGame();
    }
    

// GUI Mode


    /**
     * Starts Deadwood as a JavaFX application in GUI mode.
     * 
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Starting Deadwood game...");
    
            // Set the title of the window (Stage)
            primaryStage.setTitle("Deadwood");

            // Set the stage to the GameGUIView
            GameGUIView guiView = GameGUIView.getInstance();
            guiView.setStage(primaryStage);
            
            // Ensure the board is created and displayed first
            guiView.createBoard();
            guiView.showBoard();
            System.out.println("Board should be visible now.");
    
            // Call the rest of the game initialization
            initializeGame(primaryStage);
    
        } catch (Exception e) {
            System.err.println("Error during game initialization: " + 
                               e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize and start the game in GUI mode
     * 
     * @param primaryStage
     */
    private void initializeGame(Stage primaryStage) {
        try {
            
            GameGUIView view = initializeView(primaryStage);

            // If the view is GameGUIView, handle GUI initialization
            if (view instanceof GameGUIView) {
                GameGUIView guiView = (GameGUIView) view;

                // Step 1: Get the number of players asynchronously
                guiView.getNumPlayersAsync().thenAccept(numPlayers -> {
                    // Ensure everything runs on the JavaFX application thread
                    Platform.runLater(() -> {
                        // Step 2: Initialize the model with the numPlayers
                        initializeAndStartGame(numPlayers, view);
                    });
                });
            }
            
        } catch (Exception e) {
            System.err.println("An error occurred during GUI initialization: " +
                               e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the GameView in GUI mode.
     * 
     * @param primaryStage
     * @return the initialized view
     */
    private GameGUIView initializeView(Stage primaryStage) {
        GameGUIView view = null;
        try {
            view = GameGUIView.getInstance();
            view.setStage(primaryStage);
        } catch (Exception e) {
            System.err.println("Error initializing GameGUIView: " + 
                               e.getMessage());
            e.printStackTrace();
        }
        return view;
    }

    /**
     * Handle game initialization and start in GUI mode
     * 
     * @param numPlayers
     * @param view
     */
    private void initializeAndStartGame(int numPlayers, GameView view) {
        GameModel model = GameModel.getInstance();
        model.initModel(
            numPlayers, 
            config.getProperty("boardXMLFilePath"), 
            config.getProperty("cardsXMLFilePath")
        );
        GameController controller = new GameController();
        controller.initializeGame(
            model, 
            view, 
            config.getProperty("boardXMLFilePath"), 
            config.getProperty("cardsXMLFilePath")
        );

        // Step 3: Create the board and all its elements based on the model
        Platform.runLater(() -> {
            controller.createBoardAndElements(() -> {
                // Step 4: Run the controller's playDays method on JavaFX thread
                Platform.runLater(() -> {
                    controller.playDays();
                    // Step 6: After playDays completes, run scoreGame method
                    Platform.runLater(() -> {
                        controller.scoreGame();
                    });
                });
            });
        });
    }
    
}