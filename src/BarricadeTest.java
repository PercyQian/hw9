import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Set;
import java.util.Map;
import java.util.logging.Logger;

// Test class for Barricade
public class BarricadeTest {

    private final Logger logger = Logger.getLogger(Barricade.class.getName());
    private final LoggerTestingHandler handler = new LoggerTestingHandler();

    @Before
    public void setup() {
        logger.addHandler(handler);
    }

    @Test
    public void testSafeGetReturnsValueAndNoWarning() {
        RoamingMap<Indexes, String> map = new RoamingMap<>();
        Indexes key = new Indexes(1, 1);
        map.put(key, "value1");
        handler.clearLogRecords();
        Barricade.StateRecoveryOptional<String> result = Barricade.safeGet(map, key);
        assertEquals("value1", result.value());
        assertNull(result.exception());
        assertFalse(handler.getLastLog().isPresent());
    }

    @Test
    public void testSafePutAddAndReplace() {
        RoamingMap<Indexes, Integer> map = new RoamingMap<>();
        Indexes k1 = new Indexes(0, 0);
        Indexes k2 = new Indexes(0, 1);
        // 添加新键
        Barricade.StateRecoveryOptional<Integer> res1 = Barricade.safePut(map, k1, 10);
        assertNull(res1.value());
        // 替换已有键
        Barricade.StateRecoveryOptional<Integer> res2 = Barricade.safePut(map, k1, 20);
        assertEquals(Integer.valueOf(10), res2.value());
        // 添加另一个新键
        Barricade.StateRecoveryOptional<Integer> res3 = Barricade.safePut(map, k2, 30);
        assertNull(res3.value());
        // 验证 map 中的值
        assertEquals(Integer.valueOf(20), map.get(k1));
        assertEquals(Integer.valueOf(30), map.get(k2));
        assertFalse(handler.getLastLog().isPresent());
    }

    @Test
    public void testCorrectSizeMatchesMapSize() {
        RoamingMap<String, Integer> map = new RoamingMap<>();
        assertEquals(0, Barricade.correctSize(map));
        map.put("A", 1);
        map.put("B", 2);
        assertEquals(2, Barricade.correctSize(map));
        assertFalse(handler.getLastLog().isPresent());
    }

    @Test
    public void testCorrectKeySetAndEntrySetUnmodifiable() {
        RoamingMap<Integer, String> map = new RoamingMap<>();
        map.put(1, "one");
        map.put(2, "two");
        Set<Integer> keys = Barricade.correctKeySet(map);
        Set<Map.Entry<Integer, String>> entries = Barricade.correctEntrySet(map);
        assertEquals(map.keySet(), keys);
        assertEquals(map.entrySet(), entries);
        try {
            keys.add(3);
            fail("correctKeySet should return an unmodifiable set");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            entries.clear();
            fail("correctEntrySet should return an unmodifiable set");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testCorrectStringRepresentationMatchesToString() {
        RoamingMap<Integer, String> map = new RoamingMap<>();
        map.put(1, "one");
        map.put(2, "two");
        handler.clearLogRecords();
        String expected = map.toString();
        String result = Barricade.correctStringRepresentation(map);
        assertEquals(expected, result);
        assertFalse(handler.getLastLog().isPresent());
    }
}
