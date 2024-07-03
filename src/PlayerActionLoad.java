import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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
        // display the current list of save game names
        view.showMessage("Current saved games");
        
        // print the filenames of all .savegame files in the current directory
        try (Stream<Path> paths = Files.walk(Paths.get("./saved"))) {
            paths
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .filter(file -> file.getName().endsWith(".deadwood"))
                .forEach(file -> {
                    String filename = file.getName();
                    // clip off the .deadwood extension
                    String trimmedFilename = filename.substring(0, filename.length() - ".deadwood".length());
                    System.out.println(trimmedFilename);
                });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get user input filename to save
        view.showMessage("Enter saved game name to load:");
        String filename = view.getPlayerInput();

        // Append ".deadwood" extension and construct full path
        String fullPath = "./saved/" + filename + ".deadwood";
        File file = new File(fullPath);

        // Check if the file exists
        if (!file.exists()) {
            view.showMessage("Saved game not found.");
            return false;
        }

        // Load the game from the file
        try (FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn)) {
            GameModel loadedModel = (GameModel) in.readObject();
            // Assuming there's a method to set the current model to the loaded one
            model.setModel(loadedModel);
            view.showMessage("Game loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            view.showMessage("Failed to load the game.");
        }

        return false;
    }

}
