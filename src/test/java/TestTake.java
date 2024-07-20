import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestTake {
    @Test
    void testTakeConstructor() {
        Area area = new Area(1, 2, 3, 4);
        Take take = new Take(5, area);
        assertEquals(5, take.getNumber());
        assertEquals(area, take.getArea());
    }

    @Test
    void testGetNumber() {
        Area area = new Area(6, 7, 8, 9);
        Take take = new Take(10, area);
        assertEquals(10, take.getNumber());
    }

    @Test
    void testGetArea() {
        Area area = new Area(11, 12, 13, 14);
        Take take = new Take(15, area);
        assertEquals(area, take.getArea());
    }
}