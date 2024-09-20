import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

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

    // Task queue for sequential execution
    private final Queue<Runnable> taskQueue = new LinkedList<>();
    private boolean isProcessing = false;

    private GameController controller;

    private Integer numPlayers = null;
    
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
        System.out.println("\n");
        System.out.println("Usage: java Deadwood [OPTION]");
        System.out.println("Options:");
        System.out.println("  --cli      Run the game in Command Line " + 
                           "Interface (CLI) mode.");
        System.out.println("  --help     Display this help menu.\n");
        System.out.println("If no option is provided, the game will run in" + 
                           " GUI mode by default.\n");
    }


// ============================================================
// CLI Mode
// ============================================================


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
     * @param view the view to register as an observer
     * @return the initialized model
     */
    private static GameModel initializeModel(GameView view) {
        GameModel model = GameModel.getInstance();
        model.registerObserver(view);
        return model;
    }

    /**
     * Initializes the CLI controller.
     *
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
     * @param controller the game controller
     */
    private static void playGame(GameController controller) {
        controller.playDays();
        controller.scoreGame();
    }
    

// ============================================================
// GUI Mode
// ============================================================


    /**
     * Starts Deadwood as a JavaFX application in GUI mode.
     * 
     * @param primaryStage the primary stage for the application
     */
    @Override
    public void start(Stage primaryStage) {
        try {    
            // Set the title of the window (Stage)
            primaryStage.setTitle("Deadwood");

            // Set the icon for the application
            Image icon = new Image("deadwood_appicon.png");
            primaryStage.getIcons().add(icon);

            // Initialize GUI view (Sets the stage for the GameGUIView)
            initializeView(primaryStage);
            
            // Add all tasks to the queue
            // Step 1: Get number of players
            addTask(this::runGetNumPlayers); 
            // Step 2: Initialize the model
            addTask(this::runInitModel); 
            // Step 3: Initialize the controller
            addTask(this::runInitController); 
            // Step 4: Show application window
            addTask(this::runShowBoard); 
            // Step 5: Refresh the board
            addTask(this::runRefreshView); 
            // Step 6: Add the board elements
            addTask(() -> runCreateBoardElements(controller)); 
            // Step 7: Refresh the board
            addTask(this::runRefreshView); 
            // Step 8: Play days
            addTask(() -> runPlayDays(controller)); 
            // Step 9: Score game
            // addTask(() -> runScoreGame(controller)); 

            // Start processing the tasks in the queue
            processNextTask();
    
        } catch (Exception e) {
            System.err.println(
                "Error during game initialization: " + 
                e.getMessage()
            );
            e.printStackTrace();
        }
    }

    /**
     * Initializes the GameView in GUI mode.
     * 
     * @param primaryStage the primary stage for the application
     */
    private void initializeView(Stage primaryStage) {
        try {
            GameGUIView view = GameGUIView.getInstance();
            view.setStage(primaryStage);
        } catch (Exception e) {
            System.err.println("Error initializing GameGUIView: " +
                               e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Run the getNumPlayers method on the JavaFX application thread.
     */
    private void runGetNumPlayers() {
        // Get the number of players
        GameGUIView.getInstance().getNumPlayersFuture().thenAccept(players -> {
            this.numPlayers = players;
            processNextTask();
        });
    }

    /**
     * Run the initializeModel method on the JavaFX application thread.
     */
    private void runInitModel() {
        // Register the GUI view as an observer of the model
        GameModel.getInstance().registerObserver(GameGUIView.getInstance());
        // Initialize the model
        GameModel.getInstance().initModel(
            this.numPlayers,
            config.getProperty("boardXMLFilePath"),
            config.getProperty("cardsXMLFilePath")
        );
        processNextTask(); // Continue to the next task
    
    }

    /**
     * Run the initializeGame method on the JavaFX application thread.
     */
    private void runInitController() {
        controller = new GameController();
        controller.initializeGame(
            GameModel.getInstance(), 
            GameGUIView.getInstance(), 
            config.getProperty("boardXMLFilePath"), 
            config.getProperty("cardsXMLFilePath")
        );
        // Continue to the next task
        processNextTask();
    }

    /**
     * Run the showBoard method on the JavaFX application thread.
     */
    private void runShowBoard() {
        GameGUIView.getInstance().showBoard();
        processNextTask(); // Continue to the next task
    }

    /**
     * Run the refreshView method on the JavaFX application thread.
     */
    private void runRefreshView() {
        GameGUIView.getInstance().refreshView();
        processNextTask(); // Continue to the next task
    }

    /**
     * Run the createBoardElements method on the JavaFX application thread.
     * 
     * @param controller the game controller
     */
    private void runCreateBoardElements(GameController controller) {
        controller.createBoardElements();
        
        processNextTask(); // Continue to the next task
    }

    /**
     * Run the playDays method on the JavaFX application thread.
     * 
     * @param controller the game controller
     */
    public void runPlayDays(GameController controller) {
        controller.playDaysGUI().thenRun(() -> {
            Platform.runLater(controller::scoreGame);  // GUI-specific end
        });
    }

    // /**
    //  * Run the scoreGame method on the JavaFX application thread.
    //  * 
    //  * @param controller the game controller
    //  */
    // private void runScoreGame(GameController controller) {
    //     controller.scoreGame();
    // }
    
    /**
     * Add a new task to the JavaFX application thread queue
     * @param task the task to add
     */
    private void addTask(Runnable task) {
        taskQueue.offer(task);
    }

    /**
     * Process the next task in the JavaFX application thread queue
     */
    private void processNextTask() {
        if (isProcessing) return; // Already processing a task
        
        Runnable nextTask = taskQueue.poll();
        
        if (nextTask != null) {
            isProcessing = true;
            Platform.runLater(() -> {
                try {
                    nextTask.run(); // Execute the current task
                } finally {
                    isProcessing = false; // Mark processing as finished
                    processNextTask(); // Continue to the next task
                }
            });
        }
    }

}
