import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
        // Get user input filename to save
        String filename = view.getPlayerInput("Enter filename to save game:");
    
        // Append ".save" extension and prepend "saved/" directory to the filename
        String directoryPath = "saved/";
        filename = directoryPath + filename + ".save";
    
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
            String overwrite = view.getPlayerInput("File exists. Overwrite? (y/n): ");
    
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
