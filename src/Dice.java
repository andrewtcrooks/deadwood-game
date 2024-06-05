/**
 * A Dice object represents a six-sided dice that can be rolled to generate a random value between 1 and 6.
 */
public class Dice {
    private int value;

    /**
     * Constructs a new Dice with the given value.
     *
     * @param value the value of the Dice
     */
    Dice(int value) {
        this.value = value;
    }

    /**
     * Returns the value of the Dice.
     *
     * @return the value of the Dice
     */
    public int getValue() {
        return value;
    }

    /**
     * Rolls the Dice.
     */
    public void roll() {
        value = (int) (Math.random() * 6) + 1;
    }
}
