import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.io.OutputStream;

/**
 * Custom output stream for the console.
 */
class ConsoleOutputStream extends OutputStream {
    private final TextArea textArea;


    /**
     * Constructor for the console output stream.
     * 
     * @param textArea the text area
     */
    public ConsoleOutputStream(TextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * Writes the byte to the text area.
     * 
     * @param b the byte to write
     */
    @Override
    public void write(int b) {
        Platform.runLater(() -> {
            textArea.appendText(String.valueOf((char) b));
        });
    }

}