import java.io.PrintStream;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Manages the console for the game.
 */
public class ConsoleManager {

    
    /**
     * Constructor for the console manager.
     */
    public ConsoleManager() {
    }

    /**
     * Create the console.
     * 
     * @param bPane the layered pane
     * @param boardLabel the board label
     * @param X the x-coordinate of the console
     * @param Y the y-coordinate of the console
     * @param WIDTH the width of the console
     * @param HEIGHT the height of the console
     */
    public void createConsole(
        JLayeredPane bPane, 
        JLabel boardLabel, 
        int X, 
        int Y, 
        int WIDTH, 
        int HEIGHT,
        int layer
    ) {
        JTextArea console = new JTextArea();
        console.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setBounds(X, Y, WIDTH, HEIGHT);
        scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        );
        scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        
        // Add a DocumentListener to scroll to the bottom when new text is added
        console.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scrollToBottom();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                scrollToBottom();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                scrollToBottom();
            }

            private void scrollToBottom() {
                console.setCaretPosition(console.getDocument().getLength());
            }
        });

        bPane.add(scrollPane, new Integer(layer));
        System.setOut(new PrintStream(new ConsoleOutputStream(console)));
    }

}