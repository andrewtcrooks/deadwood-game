import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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
        try (Stream<Path> paths = Files.walk(Paths.get("./saved"))) {
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
     * Saves the game to a file.
     * 
     * @param filename the name of the file
     * @param model the game model
     * @param view the game view
     * @return true if the game was saved successfully, false otherwise
     */
    private void saveGameToFile(String filename, GameModel model, GameView view) {
        // Validate filename: only allow alphanumeric characters and dashes/underscores
        if (!filename.matches("^[a-zA-Z0-9_-]+$")) {
            view.showMessage("Invalid filename. Only alphanumeric characters, dashes, and underscores are allowed.");
            return;
        }
        // construct the full path to the saved game file
        String directoryPath = "./saved/";
        filename = directoryPath + filename + ".deadwood";
        // create the directory if it does not exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // check if the file exists
        File file = new File(filename);
        if (file.exists()) {
            view.showMessage("File exists. Overwrite? (y/n): ");
            String overwrite = view.getPlayerInput();
            if (!overwrite.equalsIgnoreCase("y")) {
                view.showMessage("Save operation cancelled.");
                return;
            }
        }
        // save the game to the file
        try (FileOutputStream fileOut = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(model);
            view.showMessage("Game saved successfully.");
        } catch (IOException i) {
            i.printStackTrace();
            view.showMessage("Error saving game.");
        }
        return;
    }
}
