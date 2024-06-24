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
    private int rehearsalTokens;
    private boolean hasRole;
    private boolean onCard;
    private Role role;
    private boolean active;

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
        this.active = false;
    }

    /**
     * Returns the ID of the player.
     *
     * @return the ID of the player
     */
    public int getID() {
        return id;
    }

    /**
     * Returns the rank of the player.
     *
     * @return the rank of the player
     */
    public int getRank() {
        return rank;
    }

    /**
     * Returns the amount of money the player has.
     *
     * @return the amount of money the player has
     */
    public int getMoney() {
        return money;
    }

    /**
     * Returns the number of credits the player has.
     *
     * @return the number of credits the player has
     */
    public int getCredits() {
        return this.credits;
    }

    /**
     * Returns the location of the player.
     *
     * @return the location of the player
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Returns the number of rehearsal tokens the player has.
     *
     * @return the number of rehearsal tokens the player has
     */
    public int getRehearsalTokens() {
        return this.rehearsalTokens;
    }

    /**
     * Returns whether the player has a role.
     *
     * @return true if the player has a role, false otherwise
     */
    public boolean getHasRole() {
        return this.hasRole;
    }

    /**
     * Returns whether the player has a role on a card.
     *
     * @return true if the player has a role on a card, false otherwise
     */
    public boolean getOnCard() {
        return this.onCard;
    }

    /**
     * Returns the role the player has.
     *
     * @return the role the player has
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Returns whether the player is active.
     *
     * @return true if the player is active, false otherwise
     */
    public boolean getActive() {
        return this.active;
    }

    /**
     * Adds to the amount of money the player has.
     *
     * @param money the amount of money to add for the player
     */
    public void addMoney(int money) {
        this.money += money;
    }

    /**
     * Decreases the amount of money the player has.
     *
     * @param money the amount of money to subtract from the player
     */
    public void decreaseMoney(int money) {
        this.money -= money;
    }

    /**
     * Adds to the number of credits the player has.
     *
     * @param credits the number of credits to add for the player
     */
    public void addCredits(int credits) {
        this.credits += credits;
    }

    /**
     * Decreases the number of credits the player has.
     *
     * @param credits the number of credits to subtract from the player
     */
    public void decreaseCredits(int credits) {
        this.credits -= credits;
    }

    /**
     * Sets the location of the player.
     *
     * @param locationName the new location of the player
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Increments the number of rehearsal tokens the player has.
     */
    public void incrementRehearsalTokens() {
        ++this.rehearsalTokens;
    }

    /**
     * Resets the number of rehearsal tokens the player has.
     */
    public void resetRehearsalTokens() {
        rehearsalTokens = 0;
    }

    /**
     * Sets the rank of the player.
     *
     * @param rank the new rank for the player
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Sets whether the player has a role.
     *
     * @param hasRole true if the player has a role, false otherwise
     */
    public void setHasRole(boolean hasRole) {
        this.hasRole = hasRole;
    }

    /**
     * Sets whether the player is on a card.
     *
     * @param onCard true if the player is on a card, false otherwise
     */
    public void setOnCard(boolean onCard) {
        this.onCard = onCard;
    }

    /**
     * Sets the role the player has.
     *
     * @param role the new role for the player
     */
    public void takeRole(Role role) {
        this.role = role;
        setHasRole(true);
        setOnCard(role.getOnCard());
        role.assignPlayer(this);
    }

    /**
     * Removes the role the player has.
     */
    public void leaveRole() {
        if (this.role != null) {
            this.role = null;
            setHasRole(false);
            setOnCard(false);
            this.role.removePlayer();
        }
    }

    /**
     * Sets whether the player is active or not.
     *
     * @param active true if the player is active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}