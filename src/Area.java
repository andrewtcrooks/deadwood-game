
/**
 * Area class represents a rectangular area on the board.
 * 
 */
public class Area {
    private int x;
    private int y;
    private int h;
    private int w;

    /**
     * Initializes a new Area with the given x, y, h, and w.
     *
     * @param x The x-coordinate of the Area.
     * @param y The y-coordinate of the Area.
     * @param h The height of the Area.
     * @param w The width of the Area.
     */
    Area(int x, int y, int h, int w) {
        this.x = x;
        this.y = y;
        this.h = h;
        this.w = w;
    }

    /**
     * Returns the x-coordinate of the Area.
     *
     * @return The x-coordinate of the Area.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the Area.
     *
     * @return The y-coordinate of the Area.
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the height of the Area.
     *
     * @return The height of the Area.
     */
    public int getH() {
        return h;
    }

    /**
     * Returns the width of the Area.
     *
     * @return The width of the Area.
     */
    public int getW() {
        return w;
    }
}