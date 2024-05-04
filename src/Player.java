
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
    private String location;
    private int rehearsalTokens;
    private boolean hasRole;
    private boolean onCard;

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
        this.location = "Trailer";
        this.rehearsalTokens = 0;
        this.hasRole = false;
        this.onCard = false;
        // TODO: Initialize other attributes
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
    String getLocation() {
        return location;
    }

    /**
     * Returns the number of rehearsal tokens the player has.
     *
     * @return the number of rehearsal tokens the player has
     */
    int getRehearsalToken() {
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
     * Sets the ID of the player.
     *
     * @param id the new ID of the player
     */
    void setID(int id) {
        this.id = id;
    }

    /**
     * Sets the rank of the player.
     *
     * @param rank the new rank of the player
     */
    void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Sets the amount of money the player has.
     *
     * @param money the new amount of money for the player
     */
    void setMoney(int money) {
        this.money = money;
    }

    /**
     * Sets the number of credits the player has.
     *
     * @param credits the new number of credits for the player
     */
    void setCredits(int credits) {
        this.credits = credits;
    }

    /**
     * Sets the location of the player.
     *
     * @param locationName the new location of the player
     */
    void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the number of rehearsal tokens the player has.
     *
     * @param rehearsalTokens the new number of rehearsal tokens for the player
     */
    void setRehearsalToken(int rehearsalTokens) {
        this.rehearsalTokens = rehearsalTokens;
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
}