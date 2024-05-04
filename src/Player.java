/**
 * This class represents a player in the game.
 * It holds information about the player's state, such as their rank, money, credits, location, and more.
 */
public class Player {
    private int id;
    private int rank;
    private int money;
    private int credits;
    private String locationName;
    private int rehearsalTokens;
    private boolean hasRole;
    private boolean onCard;

    /**
     * Constructs a new Player with the specified credits and rank.
     *
     * @param credits the initial number of credits for the player
     * @param rank the initial rank of the player
     */
    Player(int credits, int rank) {
        this.credits = credits;
        this.rank = rank;
        // TODO: Initialize other attributes
    }

    /**
     * Returns the ID of the player.
     *
     * @return the ID of the player
     */
    int getID() {
        // TODO: Implement logic to return id
        return 0;
    }

    /**
     * Returns the rank of the player.
     *
     * @return the rank of the player
     */
    int getRank() {
        // TODO: Implement logic to return rank
        return 0;
    }

    /**
     * Returns the amount of money the player has.
     *
     * @return the amount of money the player has
     */
    int getMoney() {
        // TODO: Implement logic to return money
        return 0;
    }

    /**
     * Returns the number of credits the player has.
     *
     * @return the number of credits the player has
     */
    int getCredits() {
        // TODO: Implement logic to return credits
        return 0;
    }

    /**
     * Returns the location of the player.
     *
     * @return the location of the player
     */
    String getLocation() {
        // TODO: Implement logic to return locationName
        return null;
    }

    /**
     * Returns the number of rehearsal tokens the player has.
     *
     * @return the number of rehearsal tokens the player has
     */
    int getRehearsalToken() {
        // TODO: Implement logic to return rehearsalTokens
        return 0;
    }
}