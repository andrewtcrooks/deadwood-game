import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This class represents a player in the game.
 * It holds information about the player's state, including:
 * - ID
 * - rank
 * - dollars
 * - credits
 * - rehearsal tokens
 * - whether they are active
 * - whether they have moved
 * - whether they have worked
 * - whether they have upgraded
 * It also provides methods to modify the player's state.
 */
public class Player {
    private final IntegerProperty id;
    private final IntegerProperty rank;
    private final IntegerProperty dollars;
    private final IntegerProperty credits;
    private final IntegerProperty rehearsalTokens;
    private final BooleanProperty active;
    private final BooleanProperty hasMoved;
    private final BooleanProperty hasWorked;
    private final BooleanProperty hasUpgraded;

    /**
     * Constructs a new Player with the specified rank and credits.
     *
     * @param id the ID of the player
     * @param rank the initial rank of the player
     * @param credits the initial number of credits for the player
     */
    public Player(int id, int rank, int credits) {
        this.id = new SimpleIntegerProperty(id);
        this.rank = new SimpleIntegerProperty(rank);
        this.dollars = new SimpleIntegerProperty(0);
        this.credits = new SimpleIntegerProperty(credits);
        this.rehearsalTokens = new SimpleIntegerProperty(0);
        this.active = new SimpleBooleanProperty(false);
        this.hasMoved = new SimpleBooleanProperty(false);
        this.hasWorked = new SimpleBooleanProperty(false);
        this.hasUpgraded = new SimpleBooleanProperty(false);
    }


// ============================================================
// ID
// ============================================================


    /**
     * Returns the ID of the player.
     *
     * @return the ID of the player
     */
    public int getID() {
        return this.id.get();
    }

    /**
     * Returns the property for the ID of the player.
     *
     * @return the property for the ID of the player
     */
    public IntegerProperty idProperty() {
        return this.id;
    }
    

// ============================================================
// Rank
// ============================================================


    /**
     * Returns the rank of the player.
     *
     * @return the rank of the player
     */
    public int getRank() {
        return this.rank.get();
    }
    
    /**
     * Sets the rank of the player.
     *
     * @param rank the new rank of the player
     */
    public void setRank(int rank) {
        this.rank.set(rank);
    }

    /**
     * Returns the property for the rank of the player.
     *
     * @return the property for the rank of the player
     */
    public IntegerProperty rankProperty() {
        return this.rank;
    }


// ============================================================
// Dollars
// ============================================================


    /**
     * Returns the amount of dollars the player has.
     *
     * @return the amount of dollars the player has
     */
    public int getDollars() {
        return this.dollars.get();
    }

    /**
     * Sets the amount of dollar the player has.
     *
     * @param dollars the amount of dollar for the player
     */
    public void setDollars(int dollars) {
        this.dollars.set(dollars);
    }

    /**
     * Adds to the amount of dollars the player has.
     *
     * @param amount the amount of dollars to add for the player
     */
    public void addDollars(int amount) {
        this.dollars.set(this.dollars.get() + amount);
    }

    /**
     * Decreases the amount of dollars the player has.
     *
     * @param amount the amount of dollars to subtract from the player
     */
    public void decreaseDollars(int amount) {
        this.dollars.set(this.dollars.get() - amount);
    }

    /**
     * Returns the property for the player's dollars.
     *
     * @return the property for the player's dollars
     */
    public IntegerProperty dollarsProperty() {
        return this.dollars;
    }


// ============================================================
// Credits
// ============================================================


    /**
     * Returns the number of credits the player has.
     *
     * @return the number of credits the player has
     */
    public int getCredits() {
        return this.credits.get();
    }

    /**
     * Sets the number of credits the player has.
     *
     * @param credits the number of credits for the player
     */
    public void setCredits(int credits) {
        this.credits.set(credits);
    }

    /**
     * Adds to the number of credits the player has.
     *
     * @param amount the number of credits to add for the player
     */
    public void addCredits(int amount) {
        this.credits.set(this.credits.get() + amount);
    }

    /**
     * Decreases the number of credits the player has.
     *
     * @param amount the number of credits to subtract from the player
     */
    public void decreaseCredits(int amount) {
        this.credits.set(this.credits.get() - amount);
    }

    /**
     * Returns the property for the number of credits the player has.
     *
     * @return the property for the number of credits the player has
     */
    public IntegerProperty creditsProperty() {
        return this.credits;
    }
    

// ============================================================
// Rehearsal Tokens
// ============================================================


    /**
     * Returns the number of rehearsal tokens the player has.
     *
     * @return the number of rehearsal tokens the player has
     */
    public int getRehearsalTokens() {
        return rehearsalTokens.get();
    }

    /**
     * Sets the number of rehearsal tokens the player has.
     *
     * @param tokens the number of rehearsal tokens the player has
     */
    public void setRehearsalTokens(int tokens) {
        this.rehearsalTokens.set(tokens);
    }

        /**
     * Increments the number of rehearsal tokens the player has.
     */
    public void incrementRehearsalTokens() {
        this.rehearsalTokens.set(this.rehearsalTokens.get() + 1);
    }

    /**
     * Resets the number of rehearsal tokens the player has.
     */
    public void resetRehearsalTokens() {
        this.rehearsalTokens.set(0);
    }

    /**
     * Returns the property for the number of rehearsal tokens the player has.
     * 
     * @return the property for the number of rehearsal tokens the player has
     */
    public IntegerProperty rehearsalTokensProperty() {
        return rehearsalTokens;
    }


// ============================================================
// Active
// ============================================================


    /**
     * Returns whether the player is active.
     *
     * @return true if the player is active, false otherwise
     */
    public boolean isActive() {
        return active.get();
    }

    /**
     * Sets whether the player is active or not.
     *
     * @param active true if the player is active, false otherwise
     */
    public void setActive(boolean active) {
        this.active.set(active);
    }

    /**
     * Returns the property for whether the player is active.
     * 
     * @return the property for whether the player is active
     */
    public BooleanProperty activeProperty() {
        return active;
    }


// ============================================================
// Has Moved, Has Worked, Has Upgraded
// ============================================================


    /**
     * Returns whether the player has moved.
     * @return true if the player has moved, false otherwise
     */
    public boolean getHasMoved() {
        return hasMoved.get();
    }

    /**
     * Sets whether the player has moved.
     * @param hasMoved true if the player has moved, false otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved.set(hasMoved);
    }

    /**
     * Returns the property for whether the player has moved.
     * 
     * @return the property for whether the player has moved
     */
    public BooleanProperty hasMovedProperty() {
        return hasMoved;
    }

    /**
     * Returns whether the player has worked.
     * @return true if the player has worked, false otherwise
     */
    public boolean getHasWorked() {
        return hasWorked.get();
    }

    /**
     * Sets whether the player has worked.
     * @param hasWorked true if the player has worked, false otherwise
     */
    public void setHasWorked(boolean hasWorked) {
        this.hasWorked.set(hasWorked);
    }

    /**
     * Returns the property for whether the player has worked.
     * 
     * @return the property for whether the player has worked
     */
    public BooleanProperty hasWorkedProperty() {
        return hasWorked;
    }
    
    /**
     * Returns whether the player has upgraded.
     * @return true if the player has upgraded, false otherwise
     */
    public boolean getHasUpgraded() {
        return hasUpgraded.get();
    }

    /**
     * Sets whether the player has upgraded.
     * @param hasUpgraded true if the player has upgraded, false otherwise
     */
    public void setHasUpgraded(boolean hasUpgraded) {
        this.hasUpgraded.set(hasUpgraded);
    }

    /**
     * Returns the property for whether the player has upgraded.
     * 
     * @return the property for whether the player has upgraded
     */
    public BooleanProperty hasUpgradedProperty() {
        return hasUpgraded;
    }
    

// ============================================================
// Get Score
// ============================================================


    /**
     * Returns the score of the player.
     *
     * @return the score of the player
     */
    public int getScore() {
        return 5 * getRank() + getCredits() + getDollars();
    }

}