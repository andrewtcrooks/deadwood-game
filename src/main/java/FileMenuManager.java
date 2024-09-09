import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Manages the file menu for the application.
 */
public class FileMenuManager {

    private Pane gamePane;
    private Stage stage;
    private GameGUIView gameView;

    /**
     * Constructor to initialize the FileMenuManager.
     * 
     * @param gamePane the pane where the game is displayed
     * @param stage the primary stage of the application
     * @param gameView the GameGUIView instance for showing messages
     */
    public FileMenuManager(Pane gamePane, Stage stage, GameGUIView gameView) {
        this.gamePane = gamePane;
        this.stage = stage;
        this.gameView = gameView;

        // Enable global menu bar on macOS
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
    }

    /**
     * Create the file menu with user-specified width and height.
     * 
     * @param width the width of the stage
     * @param height the height of the stage
     */
    public void createFileMenu(int width, int height) {
        // Create the menu bar
        MenuBar menuBar = new MenuBar();

        // Style the menu bar with a brown background
        menuBar.setStyle("-fx-background-color: #af734a;");

        // Create the "File" menu
        Menu fileMenu = new Menu("File");

        // Style the file menu to have white text
        fileMenu.setStyle("-fx-text-fill: white;");
        
        // Create the "Load" menu item
        MenuItem loadMenuItem = new MenuItem("Load");
        loadMenuItem.setOnAction(e -> gameView.showMessage("Load option selected"));

        // Create the "Save" menu item
        MenuItem saveMenuItem = new MenuItem("Save");
        saveMenuItem.setOnAction(e -> gameView.showMessage("Save option selected"));

        // Add menu items to the "File" menu
        fileMenu.getItems().addAll(loadMenuItem, saveMenuItem);

        // Add the "File" menu to the menu bar
        menuBar.getMenus().add(fileMenu);

        // Set the preferred size for the gamePane, based on user input
        gamePane.setPrefWidth(width);   // Set the width input from the user
        gamePane.setPrefHeight(height); // Set the height input from the user

        // Create a VBox layout and ensure it resizes correctly
        VBox layout = new VBox();
        layout.getChildren().addAll(menuBar, gamePane);

        // Ensure the VBox uses all available space for the gamePane
        VBox.setVgrow(gamePane, javafx.scene.layout.Priority.ALWAYS);

        // Create the scene with the given width and height
        Scene scene = new Scene(layout, width, height);

        // Set the new layout on the stage
        stage.setScene(scene);
    }
}
