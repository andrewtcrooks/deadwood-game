import java.util.List;

public class PlayerActionBoard implements PlayerAction {

    /**
     * Validates the board action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to validate board action
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return true;
    }

    /**
     * Executes the board action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always false to prevent player turn end
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        List<Player> players = model.getPlayers();
        view.displayBoard(players);
        return false;
    }

}
