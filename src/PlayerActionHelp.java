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
        view.showMessage("You can enter one of the following commands:");
        view.showMessage("who: Display player info");
        view.showMessage("where: Display player location");
        view.showMessage("board: Display all player locations and indicate the active player");
        view.showMessage("move: Move player to a new location");
        view.showMessage("work: Player works on a part");
        view.showMessage("upgrade: Player upgrades rank");
        view.showMessage("rehearse: Player rehearses");
        view.showMessage("act: Player acts on a role");
        view.showMessage("end: End player's turn");
        view.showMessage("quit: Quit the game");
        view.showMessage("save: Save the game");
        view.showMessage("load: Load the game");
        view.showMessage("help: Display player options");
        return false;
    }

}