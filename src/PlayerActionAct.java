/*
 * Represents the act action for the player.
 */
public class PlayerActionAct implements PlayerAction {

    /**
     * Validates the act action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true if the player has a role, false otherwise
     */
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        // Check if player has a role
        if (!player.getHasRole()) {
            view.showMessage("You do not have a role to act.");
            return false;
        }
        return true;
    }

    /**
     * Executes the act action for the player.
     *  
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return always true to end player turn
     */
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        int budget = getSceneBudget(player, model);
        int roll = performDiceRoll(player);
        Board board = model.getBoard();
        // Check if the player succeeded
        if (roll >= budget) {
            processSuccess(player, board, roll, view);
        } else {
            processFailure(player,roll, view);
        }
        checkAndWrapScene(player, model, view);
        return true;
    }

    /**
     * Gets the budget of the scene the player is acting in.
     *  
     * @param player the player
     * @return the budget of the scene
     */
    private int getSceneBudget(Player player, GameModel model) {
        return model.getBoard().getPlayerLocation(player).getSceneCard().getBudget();
    }

    /**
     * Performs a dice roll for the player.
     *  
     * @param player the player
     * @return the result of the dice roll
     */
    private int performDiceRoll(Player player) {
        Dice dice = new Dice();
        dice.roll();
        return player.getRehearsalTokens() + dice.getValue();
    }

    /**
     * Processes a successful act action for the player.
     *  
     * @param player the player
     * @param view the game view
     * @param roll the result of the dice roll
     */
    private void processSuccess(Player player, Board board, int roll, GameView view) {
        view.showMessage("You rolled a " + roll + ". Success!");
        board.getPlayerLocation(player).removeShotCounter();
        // Check if the player is on-card
        if (player.getRole().getOnCard()) {
            player.addCredits(2);
        } else {
            player.addMoney(1);
            player.addCredits(1);
        }
    }

    /**
     * Processes a failed act action for the player.
     *  
     * @param player the player
     * @param view the game view
     * @param roll the result of the dice roll
     */
    private void processFailure(Player player, int roll, GameView view) {
        view.showMessage("You rolled a " + roll + ". Failure.");
        // Check if the player is on-card
        if (!player.getRole().getOnCard()) {
            player.addMoney(1);
        }
    }

    /**
     * Checks if the scene is wrapped and wraps it if necessary.
     *  
     * @param player the player
     * @param view the game view
     */
    private void checkAndWrapScene(Player player, GameModel model, GameView view) {
        Board board = model.getBoard();
        if (board.getPlayerLocation(player).getShots() == 0) {  
            Location location = board.getPlayerLocation(player); 
            boolean anyPlayerOnCard = board.getPlayersAtLocation(location).stream()
                    .filter(Player::getHasRole) // Check if player has a role
                    .anyMatch(p -> p.getRole().getOnCard());
            if (anyPlayerOnCard) {
                view.showMessage("Bonus payout!");
            }
            model.wrapScene(location);
            board.decrementNumScenesRemaining();
            view.showMessage("The scene is wrapped.");
        }
    }

}
