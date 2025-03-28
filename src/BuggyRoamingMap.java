import java.util.*;

/**
 * A special implementation that wraps RoamingMap and can be configured to exhibit buggy behavior for testing.
 * This class helps test the error handling and warning branches in the Barricade class.
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class BuggyRoamingMap<K extends Comparable<K>, V> implements NavigableMap<K, V> {
    
    private enum BugType {
        MODIFY_STATE_ON_GET,       // modifies the state of the map when get is called
        RETURN_WRONG_VALUE_ON_GET, // returns an incorrect value when get is called
        MODIFY_STATE_ON_SIZE,      // modifies the state of the map when size is called
        RETURN_WRONG_SIZE,         // returns an incorrect size
        MODIFY_STATE_ON_TOSTRING,  // modifies the state when toString is called
        RETURN_WRONG_TOSTRING,     // returns an incorrect string representation
        FAILURE_ON_PUT             // simulates a failure during put operation
    }
    
    private final Set<BugType> enabledBugs = new HashSet<>();
    private final K bugTriggerKey;      // The key that triggers the bug
    private final V wrongValue;         // The wrong value to return for get bugs
    private final RoamingMap<K, V> map; // Delegate to RoamingMap
    
    /**
     * Creates a new BuggyRoamingMap with specific bugs enabled
     * @param bugTriggerKey the key that will trigger bugs (ignored if null)
     * @param wrongValue the incorrect value to return for get bugs
     * @param bugs the types of bugs to enable
     */
    public BuggyRoamingMap(K bugTriggerKey, V wrongValue, BugType... bugs) {
        this.map = new RoamingMap<>();
        this.bugTriggerKey = bugTriggerKey;
        this.wrongValue = wrongValue;
        if (bugs != null) {
            Collections.addAll(this.enabledBugs, bugs);
        }
    }
    
    @Override
    public V get(Object key) {
        if (bugTriggerKey != null && bugTriggerKey.equals(key)) {
            if (enabledBugs.contains(BugType.MODIFY_STATE_ON_GET)) {
                // Modify the state by adding a random entry
                map.put((K)("bug-" + System.nanoTime()), (V)"bugvalue");
            }
            
            if (enabledBugs.contains(BugType.RETURN_WRONG_VALUE_ON_GET)) {
                return wrongValue;
            }
        }
        return map.get(key);
    }
    
    @Override
    public int size() {
        if (enabledBugs.contains(BugType.MODIFY_STATE_ON_SIZE)) {
            // Modify the state by adding a random entry
            map.put((K)("bug-" + System.nanoTime()), (V)"bugvalue");
        }
        
        if (enabledBugs.contains(BugType.RETURN_WRONG_SIZE)) {
            return map.size() + 100; // Return incorrect size
        }
        
        return map.size();
    }
    
    @Override
    public V put(K key, V value) {
        if (enabledBugs.contains(BugType.FAILURE_ON_PUT)) {
            if (bugTriggerKey != null && bugTriggerKey.equals(key)) {
                // Pretend to put but don't actually do it
                return null;
            }
        }
        return map.put(key, value);
    }
    
    @Override
    public String toString() {
        if (enabledBugs.contains(BugType.MODIFY_STATE_ON_TOSTRING)) {
            // Modify the state by adding a random entry
            map.put((K)("bug-" + System.nanoTime()), (V)"bugvalue");
        }
        
        if (enabledBugs.contains(BugType.RETURN_WRONG_TOSTRING)) {
            return "BuggyMap-" + System.nanoTime();
        }
        
        return map.toString();
    }
    
    // Static factory methods to create maps with specific bugs
    
    public static <K extends Comparable<K>, V> BuggyRoamingMap<K, V> withStateModificationOnGet(K triggerKey) {
        return new BuggyRoamingMap<>(triggerKey, null, BugType.MODIFY_STATE_ON_GET);
    }
    
    public static <K extends Comparable<K>, V> BuggyRoamingMap<K, V> withWrongValueOnGet(K triggerKey, V wrongValue) {
        return new BuggyRoamingMap<>(triggerKey, wrongValue, BugType.RETURN_WRONG_VALUE_ON_GET);
    }
    
    public static <K extends Comparable<K>, V> BuggyRoamingMap<K, V> withStateModificationOnSize() {
        return new BuggyRoamingMap<>(null, null, BugType.MODIFY_STATE_ON_SIZE);
    }
    
    public static <K extends Comparable<K>, V> BuggyRoamingMap<K, V> withWrongSize() {
        return new BuggyRoamingMap<>(null, null, BugType.RETURN_WRONG_SIZE);
    }
    
    public static <K extends Comparable<K>, V> BuggyRoamingMap<K, V> withStateModificationOnToString() {
        return new BuggyRoamingMap<>(null, null, BugType.MODIFY_STATE_ON_TOSTRING);
    }
    
    public static <K extends Comparable<K>, V> BuggyRoamingMap<K, V> withWrongToString() {
        return new BuggyRoamingMap<>(null, null, BugType.RETURN_WRONG_TOSTRING);
    }
    
    public static <K extends Comparable<K>, V> BuggyRoamingMap<K, V> withFailureOnPut(K triggerKey) {
        return new BuggyRoamingMap<>(triggerKey, null, BugType.FAILURE_ON_PUT);
    }
    
    // Remaining NavigableMap interface methods - delegate to map
    
    @Override
    public Entry<K, V> lowerEntry(K key) {
        return map.lowerEntry(key);
    }

    @Override
    public K lowerKey(K key) {
        return map.lowerKey(key);
    }

    @Override
    public Entry<K, V> floorEntry(K key) {
        return map.floorEntry(key);
    }

    @Override
    public K floorKey(K key) {
        return map.floorKey(key);
    }

    @Override
    public Entry<K, V> ceilingEntry(K key) {
        return map.ceilingEntry(key);
    }

    @Override
    public K ceilingKey(K key) {
        return map.ceilingKey(key);
    }

    @Override
    public Entry<K, V> higherEntry(K key) {
        return map.higherEntry(key);
    }

    @Override
    public K higherKey(K key) {
        return map.higherKey(key);
    }

    @Override
    public Entry<K, V> firstEntry() {
        return map.firstEntry();
    }

    @Override
    public Entry<K, V> lastEntry() {
        return map.lastEntry();
    }

    @Override
    public Entry<K, V> pollFirstEntry() {
        return map.pollFirstEntry();
    }

    @Override
    public Entry<K, V> pollLastEntry() {
        return map.pollLastEntry();
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return map.descendingMap();
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return map.navigableKeySet();
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return map.descendingKeySet();
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return map.subMap(fromKey, fromInclusive, toKey, toInclusive);
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return map.headMap(toKey, inclusive);
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return map.tailMap(fromKey, inclusive);
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return map.subMap(fromKey, toKey);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return map.headMap(toKey);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return map.tailMap(fromKey);
    }

    @Override
    public Comparator<? super K> comparator() {
        return map.comparator();
    }

    @Override
    public K firstKey() {
        return map.firstKey();
    }

    @Override
    public K lastKey() {
        return map.lastKey();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }
} 