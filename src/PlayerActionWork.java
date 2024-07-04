import java.util.ArrayList;
import java.util.List;

/*
 * Represents the work action for the player.
 */
public class PlayerActionWork implements PlayerAction {

    /**
     * Validates the work action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true if the player can work, false otherwise
     */
    public boolean validate(Player player, GameModel model, GameView view) {
        // Check if the player is in the Casting Office
        if (player.getLocation().getName().equals("Casting Office")) {
            view.showMessage("There is no work in the Casting Office.");
            return false;
        }
        // Checkif the player is in their Trailer
        if (player.getLocation().getName().equals("Trailer")) {
            view.showMessage("There is no work in your Trailer.");
            return false;
        }
        // Check if location is wrapped
        if (player.getLocation().isWrapped()) {
            view.showMessage("Location is wrapped. No more work available until tomorrow.");
            return false;
        }
        // Check if the player has already has a role
        if (player.getHasRole()) {
            String roleName = player.getRole().getName();
            String roleLine = player.getRole().getLine();
            boolean onCard = player.getRole().getOnCard();
            if (onCard) {
                view.showMessage(roleName + " - \"" + roleLine + "\"");
            } else {
                view.showMessage(roleName + " - \"" + roleLine + "\" (for scale)");
            }
            
            return false;
        }
        return true;
    }

    /**
     * Executes the work action for the player.
     *
     * @param player the player
     * @param model the game model
     * @param view the game view
     * @return true to end turn if player has previously moved, false otherwise
     */
    public boolean execute(Player player, GameModel model, GameView view) {
        // Make a list of roles the player can work
        view.showMessage("You can work the following roles:");
        List<Role> allRoles = new ArrayList<>();
        for (Role role : player.getLocation().getRoles()) {
            if (role.getRank() <= player.getRank() && !role.isOccupied()) {
                allRoles.add(role);
            }
        }
        // return false if there are no roles to work
        if (allRoles.isEmpty()) {
            view.showMessage("There are no roles available for you to work.");
            return false;
        }
        // Sort roles by rank, then by "for scale"
        allRoles.sort((role1, role2) -> {
            int rankCompare = Integer.compare(role1.getRank(), role2.getRank());
            if (rankCompare == 0) {
                // If ranks are equal, sort by "for scale" (false first)
                return Boolean.compare(role1.getOnCard(), role2.getOnCard());
            }
            return rankCompare;
        });
        // Print roles with rank and "for scale" tag as needed
        for (Role role : allRoles) {
            System.out.print(role.getName());
            if (!role.getOnCard()) {
                System.out.println(" (for scale)");
            } else {
                System.out.println();
            }
            System.out.print(" -> Rank " + role.getRank());
        }
        // Get player input
        String roleString = view.getPlayerInput();
        // Find the role the player wants to work
        Role role = allRoles.stream()
                    .filter(r -> r.getName().equals(roleString))
                    .findFirst()
                    .orElse(null);
        // process the role input
        if (role == null) {
            view.showMessage("Invalid role.");
        }else{
            // Set the player's role
            player.takeRole(role);
            // Set the role's player
            role.assignPlayer(player);
            view.showMessage("You are now working the role of " + role.getName());
            // set player has worked
            player.setHasWorked(true);
        }
        // Check if the player has already moved during their turn
        if (player.getHasMoved()) {
            return true; // End the turn if the player has already moved
        }
        return false;
    }

}
