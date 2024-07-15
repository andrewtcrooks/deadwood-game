import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * Deadwood by Andrew Crooks
 *  
 * This class is the main class for the Deadwood application.
 * It creates the model, view, and controller for the game.
 * It also initializes the controller and runs the application.
 */
public class Deadwood {
    private static Properties config = new Properties();

    
    /**
     * The main method for Deadwood.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        loadConfiguration();
        try {
            GameView view = initializeView();
            GameModel model = initializeModel(view);
            GameController controller = initializeController(model, view);
            playGame(controller);
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
            // Load the config.properties file
            config.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initializes the game view.
     * 
     * @return the initialized view
     */
    private static GameView initializeView() {
        GameView view = GameCLIView.getInstance();
        view.showMessage("Welcome to Deadwood! Let's play!");
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
     * Plays a game of Deadwood.
     * 
     * @param controller the controller to use
     */
    private static void playGame(GameController controller) {
        controller.playDays();
        controller.scoreGame();
    }

}