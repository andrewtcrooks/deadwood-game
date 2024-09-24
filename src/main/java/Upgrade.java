/**
 * Represents an Upgrade in the game.
 */
public class Upgrade {
    private int level;
    private String currency;
    private int amt;
    private Area area;

    /**
     * Constructs a new Upgrade with the given parameters.
     *
     * @param level    the level of the upgrade
     * @param currency the type of currency required
     * @param amt      the amount of currency required
     * @param area     the area where the upgrade button should be placed
     */
    public Upgrade(int level, String currency, int amt, Area area) {
        this.level = level;
        this.currency = currency;
        this.amt = amt;
        this.area = area;
    }


// Getters


    /**
     * Returns the level of the upgrade.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the type of currency required for the upgrade.
     *
     * @return the currency ("dollar" or "credit")
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Returns the amount of currency required for the upgrade.
     *
     * @return the amount
     */
    public int getAmt() {
        return amt;
    }

    /**
     * Returns the area where the upgrade button should be placed.
     *
     * @return the area
     */
    public Area getArea() {
        return area;
    }

}

