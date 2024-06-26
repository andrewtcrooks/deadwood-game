import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * Deadwood.java by Andrew Crooks
 *  
 * This class is the main class for the Deadwood application.
 * It creates the model, view, and controller for the game.
 * It also initializes the controller and runs the application.
 */
public class Deadwood {
    private static Properties config = new Properties();

    public static void main(String[] args) {
        loadConfiguration();
        try {
            GameView view = initializeView();
            GameModel model = initializeModel(view);
            GameController controller = initializeController(model, view);
            startGame(controller);
        } catch (Exception e) {
            System.err.println("An error occurred during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load the configuration file.
     */
    private static void loadConfiguration() {
        try (InputStream input = Deadwood.class.getClassLoader().getResourceAsStream("resources/config.properties")) {
            if (input == null) {
                throw new IOException("Unable to find config.properties");
            }
            // Load a properties file from class path
            config.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initializes the game view.
     */
    private static GameView initializeView() {
        GameView view = GameCLIView.getInstance();
        view.displayWelcomeMessage();
        return view;
    }

    /**
     * Initializes the game model.
     */
    private static GameModel initializeModel(GameView view) {
        GameModel model = GameModel.getInstance();
        model.registerObserver(view);
        return model;
    }

    /**
     * Initializes the game controller.
     */
    private static GameController initializeController(GameModel model, GameView view) {
        GameController controller = new GameController();
        controller.initializeGame(model, 
                                  view, 
                                  config.getProperty("boardXMLFilePath"), 
                                  config.getProperty("cardsXMLFilePath")
        );
        return controller;
    }

    /**
     * Start the game.
     */
    private static void startGame(GameController controller) {
        controller.playGame();
        controller.endGame();
    }

}