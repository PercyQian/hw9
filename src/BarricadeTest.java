import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Set;

// Test class for Barricade
public class BarricadeTest {

    private final Logger logger = Logger.getLogger(Barricade.class.getName());
    private final LoggerTestingHandler handler = new LoggerTestingHandler();
    private RoamingMap<String, Integer> testMap;

    @Before
    public void setup() {
        logger.addHandler(handler);
        testMap = new RoamingMap<>();
    }

    @Test
    public void testGetWithStateVar() {
        // Test with empty map
        Barricade.StateRecoveryOptional<Integer> emptyResult = Barricade.getWithStateVar(testMap, "key1");
        assertNull("Value should be null for non-existent key", emptyResult.value());
        
        // Put a value and retrieve it
        testMap.put("key1", 100);
        Barricade.StateRecoveryOptional<Integer> result = Barricade.getWithStateVar(testMap, "key1");
        assertEquals("Should retrieve correct value", Integer.valueOf(100), result.value());
    }
    
    @Test
    public void testCorrectSize() {
        assertEquals("Empty map should have size 0", 0, Barricade.correctSize(testMap));
        
        testMap.put("key1", 100);
        assertEquals("Map with one entry should have size 1", 1, Barricade.correctSize(testMap));
        
        testMap.put("key2", 200);
        assertEquals("Map with two entries should have size 2", 2, Barricade.correctSize(testMap));
    }
    
    @Test
    public void testPutWithStateVar() {
        // Initial put should return null for previous value
        Barricade.StateRecoveryOptional<Integer> result1 = Barricade.putWithStateVar(testMap, "key1", 100);
        assertNull("Previous value should be null", result1.value());
        
        // Verify value was stored correctly
        assertEquals("Value should be stored correctly", Integer.valueOf(100), testMap.get("key1"));
        
        // Update existing value
        Barricade.StateRecoveryOptional<Integer> result2 = Barricade.putWithStateVar(testMap, "key1", 200);
        assertEquals("Previous value should be returned", Integer.valueOf(100), result2.value());
        
        // Verify value was updated correctly
        assertEquals("Value should be updated correctly", Integer.valueOf(200), testMap.get("key1"));
    }
    
    @Test
    public void testCorrectKeySet() {
        testMap.put("key1", 100);
        testMap.put("key2", 200);
        
        Set<String> keySet = Barricade.correctKeySet(testMap);
        assertEquals("KeySet should have correct size", 2, keySet.size());
        assertTrue("KeySet should contain first key", keySet.contains("key1"));
        assertTrue("KeySet should contain second key", keySet.contains("key2"));
    }
    
    @Test
    public void testCorrectEntrySet() {
        testMap.put("key1", 100);
        testMap.put("key2", 200);
        
        Set<Map.Entry<String, Integer>> entrySet = Barricade.correctEntrySet(testMap);
        assertEquals("EntrySet should have correct size", 2, entrySet.size());
        
        boolean foundFirstEntry = false;
        boolean foundSecondEntry = false;
        
        for (Map.Entry<String, Integer> entry : entrySet) {
            if ("key1".equals(entry.getKey()) && Integer.valueOf(100).equals(entry.getValue())) {
                foundFirstEntry = true;
            }
            if ("key2".equals(entry.getKey()) && Integer.valueOf(200).equals(entry.getValue())) {
                foundSecondEntry = true;
            }
        }
        
        assertTrue("Should find first entry", foundFirstEntry);
        assertTrue("Should find second entry", foundSecondEntry);
    }
    
    @Test
    public void testCorrectStringRepresentation() {
        testMap.put("key1", 100);
        
        String representation = Barricade.correctStringRepresentation(testMap);
        assertTrue("String representation should contain key", representation.contains("key1"));
        assertTrue("String representation should contain value", representation.contains("100"));
    }
}