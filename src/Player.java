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
    private int rehearsalTokens;
    private boolean hasRole;
    private Role role;
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
        // this.location = null;
        this.rehearsalTokens = 0;
        // this.hasRole = false;
        // this.role = null;
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

    // /**
    //  * Returns whether the player has a role.
    //  *
    //  * @return true if the player has a role, false otherwise
    //  */
    // public boolean getHasRole() {
    //     return this.hasRole;
    // }

    // /**
    //  * Returns the role the player has.
    //  *
    //  * @return the role the player has
    //  */
    // public Role getRole() {
    //     return this.role;
    // }

    /**
     * Returns the rank of the role the player has.
     *
     * @return the rank of the role the player has
     */
    public int getRoleRank() {
        return this.role.getRank();
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
     * Sets the amount of money the player has.
     *
     * @param money the new amount of money for the player
     */
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * Sets the number of credits the player has.
     *
     * @param credits the new number of credits for the player
     */
    public void setCredits(int credits) {
        this.credits = credits;
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
     * Sets the rank of the player.
     *
     * @param rank the new rank for the player
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    // /**
    //  * Sets whether the player has a role.
    //  *
    //  * @param hasRole true if the player has a role, false otherwise
    //  */
    // public void setHasRole(boolean hasRole) {
    //     this.hasRole = hasRole;
    // }

    // /**
    //  * Sets the role the player has.
    //  *
    //  * @param role the new role for the player
    //  */
    // public void takeRole(Role role) {
    //     this.role = role;
    //     setHasRole(true);
    //     role.assignPlayer(this);
    // }

    // /**
    //  * Removes the role the player has.
    //  */
    // public void leaveRole() {
    //     if (this.role != null) {
            
    //         // Remove the player from the role
    //         this.role.removePlayer();

    //         // Reset the role related attributes for the player
    //         setHasRole(false);
    //         resetRehearsalTokens();
    //         this.role = null;
    //     }
    // }

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