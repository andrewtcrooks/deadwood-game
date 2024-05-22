public class Take {
    private int number;
    private Area area;

    /**
     * Initializes a new Take with the given number and area.
     *
     * @param number The number of the Take.
     * @param area The area of the Take.
     */
    Take(int number, Area area) {
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
}