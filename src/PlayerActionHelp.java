/*
 * Represents the help action for the player.
 */
public class PlayerActionHelp implements PlayerAction {

    /**
     * Validates the help action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to validate help action
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return true;
    }

    /**
     * Executes the help action for the player.
     *  
     * @param player the player to help
     * @param model the game model
     * @param view the game view
     * @return always false to prevent player turn end
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        // Display the player's options
        view.showMessage("You can enter one of the following commands:\n" +
                         "who: Display player info\n" +
                         "where: Display player location\n" +
                         "board: Display all player locations and indicate the active player\n" +
                         "move: Move player to a new location\n" +
                         "work: Player works on a part\n" +
                         "upgrade: Player upgrades rank\n" +
                         "rehearse: Player rehearses\n" +
                         "act: Player acts on a role\n" +
                         "end: End player's turn\n" +
                         "quit: Quit the game\n" +
                         "save: Save the game\n" +
                         "load: Load the game\n" +
                         "help: Display player options");
        return false;
    }

}