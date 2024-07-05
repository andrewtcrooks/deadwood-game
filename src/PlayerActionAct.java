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
        // Roll the dice
        Dice dice = new Dice();
        dice.roll();
        int rehearsalTokens = player.getRehearsalTokens();
        int roll = rehearsalTokens + dice.getValue();

        // Get the budget of the scene at the player's location
        int budget = player.getLocation().getSceneCard().getBudget();

        // Compare the dice roll to the budget
        if (roll >= budget) {
            // If the roll is equal to or higher than the budget, the player succeeds
            view.showMessage("You rolled a " + roll + ". Success!");

            // Remove one shot counter from the location
            player.getLocation().removeShotCounter();

            // Check if the role is on the card
            if (player.getRole().getOnCard()) {
                // If the role is on the card, give the player +2 credits
                player.addCredits(2);
            } else {
                // If the role is not on the card, give the player +1 dollar and +1 credit
                player.addMoney(1);
                player.addCredits(1);
            }
        } else {
            // If the roll is lower than the budget, the player fails
            view.showMessage("You rolled a " + roll + ". Failure.");

            // Check if the role is not on the card
            if (!player.getRole().getOnCard()) {
                // If the role is not on the card, give the player +1 dollar
                player.addMoney(1);
            }
        }

        // Check if the location has no more shot counters
        if (player.getLocation().getShots() == 0) {
            // Check if any of the players at the location are onCard
            boolean anyPlayerOnCard = false;
            for (Player p : player.getLocation().getPlayers()) {
                if (p.getRole().getOnCard()) {
                    anyPlayerOnCard = true;
                    break;
                }
            }
            // If there is a player onCard, show message that bonus was paid out
            if (anyPlayerOnCard) {
                view.showMessage("Bonus payout!");
            }

            // If the location has no more shot counters, wrap the scene
            player.getLocation().wrapScene();
            view.showMessage("The scene is wrapped.");
        }

        return true;
    }

}
