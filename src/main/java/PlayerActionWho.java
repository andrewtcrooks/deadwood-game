/*
 * Represents the who action for the player.
 */
@SuppressWarnings("GrazieInspection")
public class PlayerActionWho implements PlayerAction{

    /**
     * Validates the who action for the player.
     *
     * @param player the player to who
     * @param model the game model
     * @param view the game view
     * @return always true to validate who action
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return true;
    }

    /**
     * Executes the who action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always false to prevent player turn end
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        // Display the player info
        String playerInfo = String.format("Player %d (rank %d, $%4d, %4dcr, %drt)", 
                player.getID(), 
                player.getRank(), 
                player.getDollars(), 
                player.getCredits(), 
                player.getRehearsalTokens()
        );
        view.showMessage(playerInfo);
        return false;
    }

}
