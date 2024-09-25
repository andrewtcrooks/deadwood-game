/*
 * Represents the rehearse action for the player.
 */
public class PlayerActionRehearse implements PlayerAction {

    /**
     * Validates the rehearse action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true if the player has a role, false otherwise
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        Board board = model.getBoard();
        // Check if player has a role
        return checkPlayerHasNoRole(player, board, view);
    }

    /**
     * Checks if the player has no role.
     *  
     * @param player the player
     * @param board the game board
     * @param view the game view
     * @return true if the player has a role, false otherwise
     */
    public boolean checkPlayerHasNoRole(Player player, Board board, GameView view) {
        if (board.getPlayerRole(player.getID()) == null) {
            view.showMessage("You do not have a role to rehearse.");
            return false;
        }
        return true;
    }
    
    /**
     * Executes the rehearse action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to end player turn
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        // Add a rehearsal token and display message
        player.incrementRehearsalTokens();
        if ( view instanceof GameCLIView) {
            ((GameCLIView) view).showMessage("Success! You got a rehearsal token!");
        }
        return true;
    }

}
