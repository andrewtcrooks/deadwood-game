import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents the command line interface view for the Deadwood game.
 * 
 */
public class GameCLIView implements GameView {
    private static GameCLIView instance;
    private Scanner scanner;


// Contructor


    /**
     * Constructs a new GameCLIView.
     */
    private GameCLIView() {
        this.scanner = new Scanner(System.in);
    }


// Singleton Pattern


    /**
     * Singleton instance of the GameCLIView.
     * 
     * @return The instance of the GameCLIView
     */
    public static synchronized GameCLIView getInstance() {
        if (instance == null) {
            instance = new GameCLIView();
        }
        return instance;
    }

    // TODO: uncomment to run Unit Tests
    /**
     * Resets the instance of the GameCLIView.
     */
    public static synchronized void reset() {
        instance = null;
    }


 // View


    /**
     * Get the player's input.
     * 
     * @return The player's input
     */
    @Override
    public String getPlayerInput() {
        String input = this.scanner.nextLine();
        return input;
    }

    /**
     *  Get the number of players.
     * 
     * @return The number of players
     */
    @Override
    public int getNumPlayers() {
        while (true) {
            System.out.println("Enter the number of players (between 2 and 8):");
            try {
                int numPlayers = this.scanner.nextInt();
                this.scanner.nextLine(); // consume the newline
                if (numPlayers >= 2 && numPlayers <= 8) {
                    return numPlayers;
                } else {
                    System.out.println("Invalid number of players.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                this.scanner.nextLine(); // discard the invalid input
            }
        }
    }

    /**
     * Display a message.
     * 
     * @param message The message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }


// Observer Pattern


    /**
     * Updates the view.
     * 
     * @param arg The argument to update the view
     */
    public void update(String name, Object arg) {
        // There is nothing to update in CLI View so this does nothing
    }

    /**
     * Sets the game action listener.
     * 
     * @param listener The game action listener
     */
    public void setGameActionListener(GameActionListener listener) {
        // There is no game action listener in CLI View so this does nothing
    }

}