import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents the command line interface view for the Deadwood game.
 */
public class GameCLIView implements GameView {
    private static GameCLIView instance;
    private Scanner scanner;


    /**
     * Constructs a new GameCLIView.
     */
    private GameCLIView() {
        this.scanner = new Scanner(System.in);
    }

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

    // TODO: comment out unless running Unit Tests
    /**
     * Resets the instance of the GameCLIView.
     */
    public static synchronized void reset() {
        instance = null;
    }

    /**
     * Updates the view.
     * 
     * @param arg The argument to update the view
     */
    public void update(Object arg) {
        // TODO: complete this stub for update method
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
                int numPlayers = scanner.nextInt();
                scanner.nextLine(); // consume the newline
                if (numPlayers >= 2 && numPlayers <= 8) {
                    return numPlayers;
                } else {
                    System.out.println("Invalid number of players.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine(); // discard the invalid input
            }
        }
    }

    /**
     * Get the player's input.
     * 
     * @return The player's input
     */
    @Override
    public String getPlayerInput() {
        String input = scanner.nextLine();
        return input;
    }

    /**
     * Display a message.
     * 
     * @param message The message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

}