public class PlayerActionEnd implements PlayerAction {

    /**
     * Validates the end action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to validate end action
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return true;
    }

    /**
     * Executes the end action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to end player turn
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        // End turn
        return true;
    }

}
