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
        // display the current list of save game names
        view.showMessage("Current saved games");
        
        // print the filenames of all .savegame files in the current directory
        try (Stream<Path> paths = Files.walk(Paths.get("./saved/"))) {
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
        view.showMessage("Enter name for new saved game:");
        String filename = view.getPlayerInput();
    
        // Prepend "saved/" directory to the filename and append ".deadwood" extension
        String directoryPath = "./saved/";
        filename = directoryPath + filename + ".deadwood";
    
        // Ensure the "saved" directory exists
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }
    
        // Create a file object with the updated filename
        File file = new File(filename);
    
        // Check if the file already exists
        if (file.exists()) {
            // Ask the user if they want to overwrite the existing file
            view.showMessage("File exists. Overwrite? (y/n): ");
            String overwrite = view.getPlayerInput();
    
            // If the user does not want to overwrite, return
            if (!overwrite.equalsIgnoreCase("y")) {
                view.showMessage("Save operation cancelled.");
                return false;
            }
        }
    
        // Proceed to save the game model to a file
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(model);
            out.close();
            fileOut.close();
            view.showMessage("Game saved successfully.");
        } catch (IOException i) {
            i.printStackTrace();
            view.showMessage("Error saving game.");}
        return false;
    }

}
