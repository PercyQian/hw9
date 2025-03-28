import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Barricade class provides safety mechanisms for operations on NavigableMap.
 * It ensures that operations like get, put, size, etc. behave correctly and
 * maintain the map's state integrity.
 */
public class Barricade {

    record StateRecoveryOptional<V>(V value, Exception exception) {}

    private static final Logger logger = Logger.getLogger(Barricade.class.getName());

    // Renamed to safeGet to better express the intent of validating state while getting a value
    final static <K extends Comparable<K>, V> StateRecoveryOptional<V> safeGet(NavigableMap<K, V> map, K key) {
        Objects.requireNonNull(map);
        Objects.requireNonNull(key);
        Map<K, V> copy = new TreeMap<>();
        copy.putAll(map);
        Set<Map.Entry<K, V>> entrySetBefore = copy.entrySet();
        V prevValue = entrySetBefore.stream()
                .filter(entry -> Objects.equals(entry.getKey(), key))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
        V value = map.get(key);
        Set<Map.Entry<K, V>> entrySetAfter = correctEntrySet(map);
        if (!Objects.equals(entrySetBefore, entrySetAfter)) {
            throw new RuntimeException("get method of NavigableMap operated incorrectly");
        }
        // If the returned value is inconsistent with expectation, log a warning and return the correct value
        if (!Objects.equals(prevValue, value)) {
            logger.log(Level.WARNING, "get method of NavigableMap returned incorrect value; correct value was used instead");
            return new StateRecoveryOptional<>(prevValue, null);
        }
        return new StateRecoveryOptional<>(value, null);
    }

    final static <K extends Comparable<K>, V> int correctSize(NavigableMap<K, V> map) {
        Objects.requireNonNull(map);
        int prevSize = Collections.unmodifiableNavigableMap(map).size();
        Set<Map.Entry<K, V>> entrySetBefore = correctEntrySet(map);
        int size = map.size();
        Set<Map.Entry<K, V>> entrySetAfter = correctEntrySet(map);
        if (!Objects.equals(entrySetBefore, entrySetAfter)) {
            throw new RuntimeException("size method of NavigableMap operated incorrectly");
        }
        if (size != prevSize) {
            logger.log(Level.WARNING, "size method of NavigableMap returned incorrect value; correct value was used instead");
            return prevSize;
        }
        return size;
    }

    // Renamed to safePut to better express the intent of validating state while putting a value
    final static <K extends Comparable<K>, V> StateRecoveryOptional<V> safePut(NavigableMap<K, V> map, K key, V value) {
        Objects.requireNonNull(map);
        Objects.requireNonNull(key);
        Objects.requireNonNull(value, "Value cannot be null");
        Map<K, V> copy = new TreeMap<>();
        copy.putAll(map);
        copy.put(key, value);
        Set<Map.Entry<K, V>> prevRoamingSet = copy.entrySet();
        V lastValue = map.put(key, value);
        V updatedValue = safeGet(map, key).value();
        Set<Map.Entry<K, V>> newRoamingSet = correctEntrySet(map);
        if (Objects.equals(updatedValue, value) && Objects.equals(prevRoamingSet, newRoamingSet)) {
            return new StateRecoveryOptional<>(lastValue, null);
        } else {
            throw new RuntimeException("put method of NavigableMap operated incorrectly");
        }
    }

    final static <K extends Comparable<K>, V> Set<K> correctKeySet(NavigableMap<K, V> map) {
        return Collections.unmodifiableMap(Objects.requireNonNull(map)).keySet();
    }

    final static <K extends Comparable<K>, V> Set<Map.Entry<K, V>> correctEntrySet(NavigableMap<K, V> map) {
        return Collections.unmodifiableMap(Objects.requireNonNull(map)).entrySet();
    }

    final static <K extends Comparable<K>, V> String correctStringRepresentation(NavigableMap<K, V> map) {
        Objects.requireNonNull(map);
        Map<K, V> copy = new TreeMap<>();
        copy.putAll(map);
        String prevRepresentation = copy.toString();
        Set<Map.Entry<K, V>> roamingSetBefore = correctEntrySet(map);
        String representation = map.toString();
        Set<Map.Entry<K, V>> roamingSetAfter = correctEntrySet(map);
        if (!Objects.equals(roamingSetBefore, roamingSetAfter)) {
            throw new RuntimeException("toString method of NavigableMap operated incorrectly");
        }
        if (!Objects.equals(prevRepresentation, representation)) {
            logger.log(Level.WARNING, "toString method of NavigableMap returned incorrect value; correct value was used instead");
            return prevRepresentation;
        }
        return representation;
    }
}
