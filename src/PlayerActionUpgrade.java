import java.util.HashMap;
import java.util.Map;

public class PlayerActionUpgrade implements PlayerAction {

    /**
     * Validates the upgrade action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true if the player can upgrade, false otherwise
     */
    public boolean validate(Player player, GameModel model, GameView view) {
        // Check if player is at the Casting Office
        if (!"Casting Office".equals(player.getLocation().getName())) {
            view.showMessage("You must be at the Casting Office to upgrade.");
            return false;
        }
        // Check if player is at highest rank
        if (player.getRank() == 6) {
            view.showMessage("You are already at the highest rank.");
            return false;
        }
        // Check if player has already upgraded
        if (player.getHasUpgraded()) {
            view.showMessage("You have already upgraded this turn.");
            return false;
        }
        // Check if player has at least 4 dollars or 5 credits
        if (player.getMoney() < 4 && player.getCredits() < 5) {
            view.showMessage("You do not have enough money or credits to upgrade.");
            return false;
        }

        return true;
    }

    /**
     * Executes the upgrade action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true to end turn if player has previously moved, false otherwise
     */
    public boolean execute(Player player, GameModel model, GameView view) {
        // Display the upgrade options for the player
        view.showMessage("You can upgrade to one of the following ranks:");
        // Define the cost of each rank in dollars
        Map<Integer, Integer> dollarCosts = new HashMap<>();
        dollarCosts.put(2, 4);
        dollarCosts.put(3, 10);
        dollarCosts.put(4, 18);
        dollarCosts.put(5, 28);
        dollarCosts.put(6, 40);
        // Define the cost of each rank in credits
        Map<Integer, Integer> creditCosts = new HashMap<>();
        creditCosts.put(2, 5);
        creditCosts.put(3, 10);
        creditCosts.put(4, 15);
        creditCosts.put(5, 20);
        creditCosts.put(6, 25);
        // Display the cost of each rank
        for (int i = player.getRank() + 1; i <= 6; i++) {
            view.showMessage(i + " - $" + dollarCosts.getOrDefault(i, 0) + " or " + 
                             creditCosts.getOrDefault(i, 0) + " credits");
        }
        // Get the user input
        int chosenRank = Integer.parseInt(view.getPlayerInput());
        // Get the player rank
        int playerRank = player.getRank();
        // Check if the chosen rank is valid
        if (chosenRank <= playerRank || chosenRank > 6) {
            view.showMessage("Invalid rank. Rank must be between " + (playerRank + 1) + " and 6 inclusively.");
            return false;
        }
        // Check if the player can afford the upgrade in dollars or credits
        boolean canAffordWithDollars = player.getMoney() >= dollarCosts.getOrDefault(chosenRank, 0);
        boolean canAffordWithCredits = player.getCredits() >= creditCosts.getOrDefault(chosenRank, 0);
        // If player can afford either payment method, get user choice
        String paymentMethod = null;
        if (canAffordWithDollars && canAffordWithCredits) {
            view.showMessage("You can afford to upgrade to this rank with either money or credits.");
            view.showMessage("Would you like to pay with money or credits?");
            paymentMethod = view.getPlayerInput();
            if (!"money".equals(paymentMethod) && !"credits".equals(paymentMethod)) {
                view.showMessage("Invalid payment method.");
                return false;
            }
        } else if (canAffordWithDollars) {
            paymentMethod = "money";
        } else if (canAffordWithCredits) {
            paymentMethod = "credits";
        }
        // Process the payment based on the chosen method
        if ("money".equals(paymentMethod)) {
            // Deduct the dollar cost
            player.setMoney(player.getMoney() - dollarCosts.get(chosenRank));
        } else if ("credits".equals(paymentMethod)) {
            // Deduct the credit cost
            player.setCredits(player.getCredits() - creditCosts.get(chosenRank));
        }
        // Upgrade the player's rank
        player.setRank(chosenRank); 
        view.showMessage("You have successfully upgraded to rank " + chosenRank + ".");
        // set player has upgraded
        player.setHasUpgraded(true);
        // Check if the player has already moved during their turn
        if (player.getHasMoved()) {
            return true; // End the turn if the player has already moved
        }
        return false;

    }
}
