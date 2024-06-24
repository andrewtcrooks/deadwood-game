/*
 * Deadwood.java by Andrew Crooks
 *  
 * This class is the main class for the Deadwood application.
 * It creates the model, view, and controller for the game.
 * It also initializes the controller and runs the application.
 */
public class Deadwood {
    private static String boardXMLFilePath = "resources/board.xml";
    private static String cardsXMLFilePath = "resources/cards.xml";
    public static void main(String[] args) {
        
        /**
         * Initializes the view for the game.
         */
        GameView view = GameCLIView.getInstance();

        /**
         * Displays the welcome message to the user.
         */
        view.displayWelcomeMessage();

        /**
         * Initializes the model for the game and requests the number of players 
         * from the user since this is the first time the model is being created.
         */
        GameModel model = GameModel.getInstance();
              
        /**
         * Registers the view with the model.
         */
        model.registerObserver(view);

        /**
         * Initializes the controller for the game.
         * The controller will interact with the model and view.
         */
        GameController controller = new GameController();

        /**
         * Initializes the game
         */
        controller.initializeGame(model, view, boardXMLFilePath, cardsXMLFilePath);

        /**
         * Starts the game and manages the game flow after the game is initialized
         * It should manage the game flow by calling the playerTurn method for each player.
         */
        controller.playGame();

        /**
         * End game, calculate scores and winner.
         */
        controller.endGame();
    }
}