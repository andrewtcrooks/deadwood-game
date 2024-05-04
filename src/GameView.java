import javax.swing.*;

/**
 * This class represents the GUI view for the game.
 */
public class GameView {
    /**
     * The main method that starts the GUI.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]){
        // Game game = new Game();
        // game.start();

        /**
         * Create a new JFrame object to represent the main window of the GUI.
         * Set the title of the window, the default close operation, and the size of the window.
         */
        JFrame frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);

        /**
         * Create a new JButton object to represent a button in the GUI.
         * Add the button to the content pane of the frame.
         */
        JButton button = new JButton("Press");
        frame.getContentPane().add(button); // Adds Button to content pane of frame

        /**
         * Make the frame visible.
         */
        frame.setVisible(true);
    }
}