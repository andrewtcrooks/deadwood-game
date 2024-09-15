/**
 * Represents a player's statistics (dollars, credits, and tokens) in the game.
 */
@SuppressWarnings("unused") 
public class PlayerStat {
    private final Integer playerID;
    private Integer dollars;
    private Integer credits;
    private Integer tokens;

    public PlayerStat(
        Integer playerID, 
        Integer dollars, 
        Integer credits, 
        Integer tokens, 
        boolean highlighted
    ) {
        this.playerID = playerID;
        this.dollars = dollars;
        this.credits = credits;
        this.tokens = tokens;
    }

    /**
     * Returns the ID of the player.
     *
     * @return the ID of the player
     */
    public Integer getPlayerID() {
        return playerID;
    }

    /**
     * Sets the number of dollars the player has.
     *
     * @param dollars the number of dollars the player has
     */
    public void setDollars(Integer dollars) {
        this.dollars = dollars;
    }

    /**
     * Returns the number of dollars the player has.
     *
     * @return the number of dollars the player has
     */
    public int getDollars() {
        return this.dollars;
    }
    
    /**
     * Sets the number of credits the player has.
     *
     * @param credits the number of credits the player has
     */
    public void setCredits(Integer credits) {
        this.credits = credits;
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
     * Sets the number of rehearsal tokens the player has.
     *
     * @param tokens the number of rehearsal tokens the player has
     */
    public void setTokens(Integer tokens) {
        this.tokens = tokens;
    }

    /**
     * Returns the number of rehearsal tokens the player has.
     *
     * @return the number of rehearsal tokens the player has
     */
    public int getTokens() {
        return this.tokens;
    }
}