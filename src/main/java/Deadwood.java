import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.application.Platform;

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

    /**
     * The main method for Deadwood.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // This makes sure the macOS menu bar and other UI elements behave properly
        System.setProperty("apple.awt.application.name", "Deadwood");
        // Start the JavaFX application lifecycle
        launch(args);  
    }

    /**
     * Starts the Deadwood application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize the Deadwood game with the primaryStage
            initialize(primaryStage);  
        } catch (Exception e) {
            System.err.println("An error occurred during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the game with the provided stage.
     * 
     * @param primaryStage the main stage of the application
     */
    private void initialize(Stage primaryStage) {
        loadConfiguration();
        // Check if running on Mac and set the system property so 
        // the File menu appears in the menu bar
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        try {
            GameView view = initializeView(primaryStage);
            GameModel model = initializeModel(view);
            GameController controller = initializeController(model, view);
            Platform.runLater(() -> {
                
            // // Delay game execution until initialization is complete
            // controller.setOnGameInitialized(() -> {
                // After initialization completes, call playDays() and scoreGame()
            
                controller.playDays();
                controller.scoreGame();
            });
            // });
        } catch (Exception e) {
            System.err.println(
                "An error occurred during initialization: " + 
                e.getMessage()
            );
            e.printStackTrace();
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
     * Initializes the game view.
     * 
     * @return the initialized view
     */
    private static GameView initializeView(Stage primaryStage) {
        GameView view = GameGUIView.getInstance();
        // Only set the stage if this is the GUI view (GameGUIView)
        if (view instanceof GameGUIView) {
            ((GameGUIView) view).setStage(primaryStage);
        }
        return view;
    }

    /**
     * Initializes the game model.
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
     * Initializes the game controller.
     * 
     * @param model the model to use
     * @param view the view to use
     * @return the initialized controller
     */
    private static GameController initializeController(
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
        // view.setGameActionListener(controller);
        return controller;
    }

//    /**
//     * Plays a game of Deadwood.
//     *
//     * @param controller the controller to use
//     */
//    private static void playGame(GameController controller) {
//        controller.playDays();
//        controller.scoreGame();
//    }

}