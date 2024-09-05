import java.io.OutputStream;
import javax.swing.JTextArea;

/**
 * Custom output stream for the console.
 */
class ConsoleOutputStream extends OutputStream {
    private final JTextArea textArea;


    /**
     * Constructor for the console output stream.
     * 
     * @param textArea the text area
     */
    public ConsoleOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * Writes the byte to the text area.
     * 
     * @param b the byte to write
     */
    @Override
    public void write(int b) {
        textArea.append(String.valueOf((char) b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

}