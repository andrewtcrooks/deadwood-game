import java.util.*;


/**
 * This class represents a player in the game.
 * It holds information about the player's state, including:
 * - rank
 * - money
 * - credits
 * - location
 * - rehearsal tokens
 * - whether they have a role
 * - whether that role is on a card
 */
public class Player {
    private int id;
    private int rank;
    private int money;
    private int credits;
    private Location location;
    private List<Location> adjLocations;
    private int rehearsalTokens;
    private boolean hasRole;
    private boolean onCard;
    private Role role;

    /**
     * Constructs a new Player with the specified rank and credits.
     *
     * @param id the ID of the player
     * @param rank the initial rank of the player
     * @param credits the initial number of credits for the player
     */
    Player(int id, int rank, int credits) {
        this.id = id;
        this.rank = rank;
        this.money = 0;
        this.credits = credits;
        this.location = null;
        this.rehearsalTokens = 0;
        this.hasRole = false;
        this.onCard = false;
        this.role = null;
    }

    /**
     * Returns the ID of the player.
     *
     * @return the ID of the player
     */
    int getID() {
        return id;
    }

    /**
     * Returns the rank of the player.
     *
     * @return the rank of the player
     */
    int getRank() {
        return rank;
    }

    /**
     * Returns the amount of money the player has.
     *
     * @return the amount of money the player has
     */
    int getMoney() {
        return money;
    }

    /**
     * Returns the number of credits the player has.
     *
     * @return the number of credits the player has
     */
    int getCredits() {
        return credits;
    }

    /**
     * Returns the location of the player.
     *
     * @return the location of the player
     */
    Location getLocation() {
        return location;
    }


    List<Location> getAdjacentLocations() {
        // @TODO: Implement logic to return adjacent locations
        return adjLocations;
    }

    /**
     * Returns the number of rehearsal tokens the player has.
     *
     * @return the number of rehearsal tokens the player has
     */
    int getRehearsalTokens() {
        return rehearsalTokens;
    }

    /**
     * Returns whether the player has a role.
     *
     * @return true if the player has a role, false otherwise
     */
    boolean getHasRole() {
        return hasRole;
    }

    /**
     * Returns whether the player has a role on a card.
     *
     * @return true if the player has a role on a card, false otherwise
     */
    boolean getOnCard() {
        return onCard;
    }

    /**
     * Returns the role the player has.
     *
     * @return the role the player has
     */
    Role getRole() {
        return role;
    }

    /**
     * Increments the rank of the player.
     */
    void incrementRank() {
        rank++;
    }

    /**
     * Adds to the amount of money the player has.
     *
     * @param money the amount of money to add for the player
     */
    void addMoney(int money) {
        this.money += money;
    }

    /**
     * Decreases the amount of money the player has.
     *
     * @param money the amount of money to subtract from the player
     */
    void decreaseMoney(int money) {
        this.money -= money;
    }

    /**
     * Adds to the number of credits the player has.
     *
     * @param credits the number of credits to add for the player
     */
    void addCredits(int credits) {
        this.credits += credits;
    }

    /**
     * Decreases the number of credits the player has.
     *
     * @param credits the number of credits to subtract from the player
     */
    void decreaseCredits(int credits) {
        this.credits -= credits;
    }

    /**
     * Sets the location of the player.
     *
     * @param locationName the new location of the player
     */
    void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Increments the number of rehearsal tokens the player has.
     */
    void incrementRehearsalTokens() {
        rehearsalTokens++;
    }

    /**
     * Resets the number of rehearsal tokens the player has.
     */
    void resetRehearsalTokens() {
        rehearsalTokens = 0;
    }

    /**
     * Sets whether the player has a role.
     *
     * @param hasRole true if the player has a role, false otherwise
     */
    void setHasRole(boolean hasRole) {
        this.hasRole = hasRole;
    }

    /**
     * Sets whether the player is on a card.
     *
     * @param onCard true if the player is on a card, false otherwise
     */
    void setOnCard(boolean onCard) {
        this.onCard = onCard;
    }

    /**
     * Sets the role the player has.
     *
     * @param role the new role for the player
     */
    void takeRole(Role role) {
        this.role = role;
        setHasRole(true);
        setOnCard(role.onCard());
        role.assignPlayer(this);
    }

    /**
     * Removes the role the player has.
     */
    public void leaveRole() {
        if (role != null) {
            this.role = null;
            setHasRole(false);
            setOnCard(false);
            role.removePlayer();
        }
    }

}