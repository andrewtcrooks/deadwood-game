import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.google.gson.Gson;

/*
 * Represents the load action for the player.
 */
public class PlayerActionLoad implements PlayerAction {

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
        view.showMessage("Current saved games:");
        try (Stream<Path> paths = Files.walk(Paths.get("./saved"))) {
            paths.filter(Files::isRegularFile)
                 .map(Path::toFile)
                 .filter(file -> file.getName().endsWith(".json"))
                 .forEach(file -> {
                     String filename = file.getName();
                     String trimmedFilename = filename.substring(0, filename.length() - ".deadwood".length());
                     view.showMessage(trimmedFilename);
                 });
        } catch (IOException e) {
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
        try {
            GameModel loadedModel = JsonUtil.loadFromJsonFile(fullPath, GameModel.class);
            model.setModel(loadedModel);
            view.showMessage("Game loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            view.showMessage("Failed to load the game.");
        }
    }

}
