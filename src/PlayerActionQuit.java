public class PlayerActionQuit implements PlayerAction {

    /**
     * Validates the end action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always returns true to validate quit action
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return true;
    }

    /**
     * Executes the quit action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return just a placeholder as game exits before use
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        // Exit game
        System.exit(0);
        return true;
    }

}
