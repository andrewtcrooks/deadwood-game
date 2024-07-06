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
    @Override
    public boolean validate(Player player, GameModel model, GameView view) {
        return isNotInCastingOffice(player, view) && // true if not in Casting Office
               isNotInTrailer(player, view) && // true if not in Trailer
               isAtUnwrappedLocation(player, view) && // true if location is not wrapped
               playerHasNoRole(player, view); // true if player has no role
    }
    
    /**
     * Checks if the player is not in the Casting Office.
     *
     * @param player the player
     * @param view the game view
     * @return true if the player is not in the Casting Office, false otherwise
     */
    private boolean isNotInCastingOffice(Player player, GameView view) {
        if (player.getLocation().getName().equals("Casting Office")) {
            view.showMessage("There is no work in the Casting Office.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the player is not in the Trailer.
     *
     * @param player the player
     * @param view the game view
     * @return true if the player is not in the Trailer, false otherwise
     */
    private boolean isNotInTrailer(Player player, GameView view) {
        if (player.getLocation().getName().equals("Trailer")) {
            view.showMessage("There is no work in your Trailer.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the player is at an unwrapped location.
     *
     * @param player the player
     * @param view the game view
     * @return true if the location is not wrapped, false otherwise
     */
    private boolean isAtUnwrappedLocation(Player player, GameView view) {
        if (player.getLocation().getIsWrapped()) {
            view.showMessage("Location is wrapped. No more work available until tomorrow.");
            return false;
        }
        return true;
    }

    /**
     * Checks if the player has a role.
     *
     * @param player the player
     * @param view the game view
     * @return true if the player has no role, false otherwise
     */
    private boolean playerHasNoRole(Player player, GameView view) {
        if (player.getHasRole()) {
            String roleName = player.getRole().getName();
            String roleLine = player.getRole().getLine();
            boolean onCard = player.getRole().getOnCard();
            String message = "Current role: " + roleName + " - \"" + roleLine + "\"";
            if (onCard) {
                view.showMessage(message);
            } else {
                view.showMessage(message + " (for scale)");
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
    @Override
    public boolean execute(Player player, GameModel model, GameView view) {
        // Make a list of roles the player can work
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
        } else {
            view.showMessage("You can work the following roles:");
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
                System.out.print(" (for scale)");
            }
            System.out.println(" -> Rank " + role.getRank());
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
        // End the turn if the player has already moved
        return player.getHasMoved();
    }

}
