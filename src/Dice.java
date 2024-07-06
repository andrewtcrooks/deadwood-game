/**
 * A Dice object represents a six-sided dice that can be rolled to generate a random value between 1 and 6.
 */
public class Dice {
    private int value;

    /**
     * Constructs a new Dice object and rolls it.
     */
    public Dice() {
        roll();
    }

    /**
     * Returns the value of the Dice.
     *
     * @return the value of the Dice
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Rolls the Dice and initializes its value.
     */
    public void roll() {
        this.value = (int) (Math.random() * 6) + 1;
    }
}
