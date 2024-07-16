import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.List;
import java.util.stream.Collectors;


/*
 * Represents the save action for the player.
 */
public class PlayerActionSave implements PlayerAction {

    /**
     * Validates the save action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to validate save action
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return true;
    }

    /**
     * Executes the save action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always false to prevent player turn end
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        displaySavedGames(view);
        String filename = getSavedGameNameForSaving(view);
        saveGameToFile(filename, model, view);
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
     * Gets the name of the saved game from the user.
     * 
     * @param view
     * @return the name of the saved game
     */
    private String getSavedGameNameForSaving(GameView view) {
        view.showMessage("Enter name for new saved game:");
        return view.getPlayerInput();
    }

    /**
     * Saves the game to a file as JSON.
     * 
     * @param filename the name of the file
     * @param model the game model
     * @param view the game view
     */
    private void saveGameToFile(String filename, GameModel model, GameView view) {
        try {
            JsonUtil.saveToJsonFile(model, "saved/" + filename + ".json");
            view.showMessage("Game saved successfully as " + filename + ".json");
        } catch (IOException e) {
            view.showMessage("Failed to save the game.");
            e.printStackTrace();
        }
    }
    
}
