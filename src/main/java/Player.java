/**
 * This class represents a player in the game.
 * It holds information about the player's state, including:
 * - ID
 * - rank
 * - money
 * - credits
 * - rehearsal tokens
 * - whether they are active
 * - whether they have moved
 * - whether they have worked
 * - whether they have upgraded
 * It also provides methods to modify the player's state.
 */
public class Player {
    private int id;
    private int rank;
    private int money;
    private int credits;
    private int rehearsalTokens;
    private boolean active;
    private boolean hasMoved;
    private boolean hasWorked;
    private boolean hasUpgraded;

    /**
     * Constructs a new Player with the specified rank and credits.
     *
     * @param id the ID of the player
     * @param rank the initial rank of the player
     * @param credits the initial number of credits for the player
     */
    public Player(int id, int rank, int credits) {
        this.id = id;
        this.rank = rank;
        this.money = 0;
        this.credits = credits;
        this.rehearsalTokens = 0;
        this.active = false;
        this.hasMoved = false;
        this.hasWorked = false;
        this.hasUpgraded = false;
    }

    /**
     * Returns the ID of the player.
     *
     * @return the ID of the player
     */
    public int getID() {
        return this.id;
    }

    /**
     * Returns the rank of the player.
     *
     * @return the rank of the player
     */
    public int getRank() {
        return this.rank;
    }

    /**
     * Returns the amount of money the player has.
     *
     * @return the amount of money the player has
     */
    public int getMoney() {
        return this.money;
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
     * Returns the number of rehearsal tokens the player has.
     *
     * @return the number of rehearsal tokens the player has
     */
    public int getRehearsalTokens() {
        return this.rehearsalTokens;
    }

    /**
     * Returns whether the player is active.
     *
     * @return true if the player is active, false otherwise
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Returns whether the player has moved.
     * @return true if the player has moved, false otherwise
     */
    public boolean getHasMoved() {
        return this.hasMoved;
    }

    /**
     * Returns whether the player has worked.
     * @return true if the player has worked, false otherwise
     */
    public boolean getHasWorked() {
        return this.hasWorked;
    }

    /**
     * Returns whether the player has upgraded.
     * @return true if the player has upgraded, false otherwise
     */
    public boolean getHasUpgraded() {
        return this.hasUpgraded;
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
     * Increments the number of rehearsal tokens the player has.
     */
    public void incrementRehearsalTokens() {
        ++this.rehearsalTokens;
    }

    /**
     * Resets the number of rehearsal tokens the player has.
     */
    public void resetRehearsalTokens() {
        this.rehearsalTokens = 0;
    }

    /**
     * Sets whether the player is active or not.
     *
     * @param active true if the player is active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Sets whether the player has moved.
     * @param hasMoved true if the player has moved, false otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Sets whether the player has worked.
     * @param hasWorked true if the player has worked, false otherwise
     */
    public void setHasWorked(boolean hasWorked) {
        this.hasWorked = hasWorked;
    }

    /**
     * Sets whether the player has upgraded.
     * @param hasUpgraded true if the player has upgraded, false otherwise
     */
    public void setHasUpgraded(boolean hasUpgraded) {
        this.hasUpgraded = hasUpgraded;
    }

    /**
     * Returns the score of the player.
     *
     * @return the score of the player
     */
    public int getScore() {
        return 5*this.rank + this.credits + this.money;
    }

}