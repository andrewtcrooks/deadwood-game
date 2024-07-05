import java.util.HashMap;
import java.util.Map;

/*
 * Represents the upgrade action for the player.
 */
public class PlayerActionUpgrade implements PlayerAction {
    // Dollar costs for each rank
    private static final Map<Integer, Integer> dollarCosts = new HashMap<>();
    // Credit costs for each rank
    private static final Map<Integer, Integer> creditCosts = new HashMap<>();
    // Initialize dollar costs and credit costs for each rank
    static {
        dollarCosts.put(2, 4); // $4 for rank 2
        dollarCosts.put(3, 10); // $10 for rank 3
        dollarCosts.put(4, 18); // $18 for rank 4
        dollarCosts.put(5, 28); // $28 for rank 5
        dollarCosts.put(6, 40); // $40 for rank 6
        creditCosts.put(2, 5); // 5 credits for rank 2
        creditCosts.put(3, 10); // 10 credits for rank 3
        creditCosts.put(4, 15); // 15 credits for rank 4
        creditCosts.put(5, 20); // 20 credits for rank 5
        creditCosts.put(6, 25); // 25 credits for rank 6
    }

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
        displayUpgradeOptions(player, view);
        int chosenRank = getUserChosenRank(view);
        if (!validateChosenRank(chosenRank, player.getRank(), view)) {
            return false;
        }
        String paymentMethod = determinePaymentMethod(player, chosenRank, view);
        // Validate payment method
        if (paymentMethod == null || (!"money".equals(paymentMethod) && !"credits".equals(paymentMethod))) {
            view.showMessage("Invalid payment method.");
            return false;
        }
        processPayment(player, chosenRank, paymentMethod);
        upgradePlayerRank(player, chosenRank, view);
        // End the turn if the player has already moved
        return player.getHasMoved();
    }

    /*
     * Displays the upgrade options for the player.
     */
    private void displayUpgradeOptions(Player player, GameView view) {
        view.showMessage("You can upgrade to one of the following ranks:");
        for (int i = player.getRank() + 1; i <= 6; i++) {
            String message = String.format("%d - $%4d or %4d credits", i, 
                                    dollarCosts.getOrDefault(i, 0), 
                                    creditCosts.getOrDefault(i, 0));
            view.showMessage(message);
        }
    }

    /* 
    * Returns users chosen rank
    */
    private int getUserChosenRank(GameView view) {
        return Integer.parseInt(view.getPlayerInput());
    }

    /*
    * Validates the chosen rank
    */
    private boolean validateChosenRank(int chosenRank, int playerRank, GameView view) {
        if (chosenRank <= playerRank || chosenRank > 6) {
            view.showMessage("Invalid rank. Rank must be between " + (playerRank + 1) + " and 6 inclusively.");
            return false;
        }
        return true;
    }

    /* 
     * Determines the payment method
     */
    private String determinePaymentMethod(Player player, int chosenRank, GameView view) {
        boolean canAffordWithDollars = player.getMoney() >= dollarCosts.getOrDefault(chosenRank, 0);
        boolean canAffordWithCredits = player.getCredits() >= creditCosts.getOrDefault(chosenRank, 0);
        if (canAffordWithDollars && canAffordWithCredits) {
            view.showMessage("You can afford to upgrade to this rank with either money or credits.");
            view.showMessage("Would you like to pay with money or credits?");
            return view.getPlayerInput();
        } else if (canAffordWithDollars) {
            return "money";
        } else if (canAffordWithCredits) {
            return "credits";
        }
        return null;
    }

    /*
    * Processes payment
    */
    private void processPayment(Player player, int chosenRank, String paymentMethod) {
        if ("money".equals(paymentMethod)) {
            player.setMoney(player.getMoney() - dollarCosts.get(chosenRank));
        } else if ("credits".equals(paymentMethod)) {
            player.setCredits(player.getCredits() - creditCosts.get(chosenRank));
        }
    }

    /*
    * Upgrades player rank
    */
    private void upgradePlayerRank(Player player, int chosenRank, GameView view) {
        player.setRank(chosenRank);
        view.showMessage("You have successfully upgraded to rank " + chosenRank + ".");
        player.setHasUpgraded(true);
    }
}
