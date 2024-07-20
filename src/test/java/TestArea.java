import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestArea {
    @Test
    void testAreaConstructor() {
        Area area = new Area(1, 2, 3, 4);
        assertEquals(1, area.getX());
        assertEquals(2, area.getY());
        assertEquals(3, area.getH());
        assertEquals(4, area.getW());
    }

    @Test
    void testGetX() {
        Area area = new Area(5, 6, 7, 8);
        assertEquals(5, area.getX());
    }

    @Test
    void testGetY() {
        Area area = new Area(9, 10, 11, 12);
        assertEquals(10, area.getY());
    }

    @Test
    void testGetH() {
        Area area = new Area(13, 14, 15, 16);
        assertEquals(15, area.getH());
    }

    @Test
    void testGetW() {
        Area area = new Area(17, 18, 19, 20);
        assertEquals(20, area.getW());
    }
}
