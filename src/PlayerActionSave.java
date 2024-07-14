import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.Writer;


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
        view.showMessage("Current saved games:");
        System.out.println(System.getProperty("user.dir"));
        try (Stream<Path> paths = Files.walk(Paths.get("saved/"))) {
            paths.filter(Files::isRegularFile)
                 .map(Path::toFile)
                 .filter(file -> file.getName().endsWith(".deadwood"))
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
