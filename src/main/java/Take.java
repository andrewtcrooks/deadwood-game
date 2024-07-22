/**
 * Take class represents a Take on a deadwood set
 */
public class Take {
    private int number;
    private Area area;
    private boolean wrapped = false;

    /**
     * Initializes a new Take with the given number and area.
     *
     * @param number The number of the Take.
     * @param area The area of the Take.
     */
    public Take(int number, Area area) {
        this.number = number;
        this.area = area;
    }

    /**
     * Returns the number of the Take.
     * 
     * @return The number of the Take.
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * Returns the area of the Take.
     * 
     * @return The area of the Take.
     */
    public Area getArea() {
        return this.area;
    }

    /**
     * Returns whether the Take is wrapped.
     * 
     * @return True if the Take is wrapped, false otherwise.
     */
    public boolean isWrapped() {
        return this.wrapped;
    }

    /**
     * Wraps the Take.
     */
    public void wrap() {
        this.wrapped = true;
    }

    /**
     * Unwraps the Take.
     */
    public void reset() {
        this.wrapped = false;
    }
}