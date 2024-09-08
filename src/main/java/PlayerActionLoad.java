import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Represents the load action for the player.
 */
public class PlayerActionLoad implements PlayerAction {

    /**
     * Constructs a PlayerActionLoad object.
     */
    public PlayerActionLoad() {
        super();
    }

    /**
     * Validates the load action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to validate load action
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return true;
    }

    /**
     * Executes the load action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always false to prevent player turn end
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        displaySavedGames(view);
        String filename = getSavedGameNameFromUser(view);
        loadGameFromFile(filename, model, view);
        return false;
    }
    
    /**
     * Displays the saved games to the player.
     * 
     * @param view
     */
    private void displaySavedGames(GameView view) {
        // Display a header to indicate the start of the saved games list
        view.showMessage("Current saved games:");
        try (Stream<Path> paths = Files.walk(Paths.get("./saved"))) {
            // Create a sorted list of saved game names by processing each file in the "./saved" directory
            List<String> sortedFileNames = paths
                .filter(Files::isRegularFile) // Ensure we only deal with files (not directories)
                .map(Path::toFile) // Convert Path to File for easier manipulation
                .filter(file -> file.getName().endsWith(".json")) // Filter to include only JSON files
                .map(file -> file.getName().substring(0, file.getName().length() - ".json".length())) // Remove the ".json" extension from the file name
                .sorted() // Sort the file names alphabetically
                .collect(Collectors.toList()); // Collect the results into a list
            
            // Display each file name in the sorted list
            sortedFileNames.forEach(view::showMessage);
        } catch (IOException e) {
            // Handle any IO exceptions that occur during directory traversal or file processing
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the saved game name from the player.
     * 
     * @param view
     * @return the saved game name
     */
    private String getSavedGameNameFromUser(GameView view) {
        view.showMessage("Enter saved game name to load:");
        return view.getPlayerInput();
    }
    
    /**
     * Loads the game from the file.
     * 
     * @param filename the saved game name
     * @param model the game model
     * @param view the game view
     * @return true if the game is loaded successfully, false otherwise
     */
    private void loadGameFromFile(String filename, GameModel model, GameView view) {
        // Validate filename: only allow alphanumeric characters and dashes/underscores
        if (!filename.matches("^[a-zA-Z0-9_-]+$")) {
            view.showMessage("Invalid filename. Only alphanumeric characters, dashes, and underscores are allowed.");
            return;
        }
        // construct the full path to the saved game file
        String fullPath = "./saved/" + filename + ".json";
        // check if the file exists
        File file = new File(fullPath);
        if (!file.exists()) {
            view.showMessage("Saved game not found.");
            return;
        }
        // load the game from the JSON file
        if (GameModel.loadFromJson(fullPath)) {
            view.showMessage("Game loaded successfully.");
        } else {
            view.showMessage("Error loading game.");
        }
    }

}
