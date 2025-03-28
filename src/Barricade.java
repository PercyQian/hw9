import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// May contain bug(s)
public class Barricade {

    public static final class StateRecoveryOptional<V> {
        private final V value;
        private final Exception exception;

        public StateRecoveryOptional(V value, Exception exception) {
            this.value = value;
            this.exception = exception;
        }

        public V value() {
            return value;
        }

        public Exception exception() {
            return exception;
        }
    }

    private static final Logger logger = Logger.getLogger(Barricade.class.getName());

    final static <K extends Comparable<K>, V> StateRecoveryOptional<V> getWithStateVar(RoamingMap<K, V> roamingMap, K key) {
        Objects.requireNonNull(roamingMap);
        Objects.requireNonNull(key);
        Map<K, V> copy = new TreeMap<>();
        copy.putAll(roamingMap);
        Set<Map.Entry<K, V>> entrySetBefore = copy.entrySet();
        V prevValue = entrySetBefore.stream().filter(entry -> Objects.equals(entry.getKey(), key)).map(Map.Entry::getValue).findFirst().orElse(null);
        V value = roamingMap.get(key);
        Set<Map.Entry<K, V>> entrySetAfter = correctEntrySet(roamingMap);
        if (!Objects.equals(entrySetBefore, entrySetAfter)) {
            throw new RuntimeException("get method of RoamingMap operated incorrectly");
        }
        if (!Objects.equals(prevValue, value)) {
            logger.log(Level.WARNING, "get method of RoamingMap returned incorrect value; correct value was used instead");
            return new StateRecoveryOptional<>(prevValue, null);
        }
        return new StateRecoveryOptional<>(value, null);
    }

    final static <K extends Comparable<K>, V> int correctSize(RoamingMap<K, V> roamingMap) {
        Objects.requireNonNull(roamingMap);
        int prevSize = Collections.unmodifiableNavigableMap(roamingMap).size();
        Set<Map.Entry<K, V>> entrySetBefore = correctEntrySet(roamingMap);
        int size = roamingMap.size();
        Set<Map.Entry<K, V>> entrySetAfter = correctEntrySet(roamingMap);
        if (!Objects.equals(entrySetBefore, entrySetAfter)) {
            throw new RuntimeException("size method of RoamingMap operated incorrectly");
        }
        if (size != prevSize) {
            logger.log(Level.WARNING, "size method of RoamingMap returned incorrect value; correct value was used instead");
            return prevSize;
        }
        return prevSize;
    }

    final static <K extends Comparable<K>, V> StateRecoveryOptional<V> putWithStateVar(RoamingMap<K, V> roamingMap, K key, V value) {
        Objects.requireNonNull(roamingMap);
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        V lastValue = roamingMap.put(key, value);
        V updatedValue = getWithStateVar(roamingMap, key).value();
        if (!Objects.equals(updatedValue, value)) {
            logger.log(Level.WARNING, "put method of RoamingMap failed to store the correct value");
            throw new RuntimeException("put method of RoamingMap operated incorrectly");
        }
        return new StateRecoveryOptional<>(lastValue, null);
    }

    final static <K extends Comparable<K>, V> Set<K> correctKeySet(RoamingMap<K, V> roamingMap) {
        return Collections.unmodifiableMap(Objects.requireNonNull(roamingMap)).keySet();
    }

    final static <K extends Comparable<K>, V> Set<Map.Entry<K, V>> correctEntrySet(RoamingMap<K, V> roamingMap) {
        return Collections.unmodifiableMap(Objects.requireNonNull(roamingMap)).entrySet();
    }

    final static <K extends Comparable<K>, V> String correctStringRepresentation(RoamingMap<K, V> roamingMap) {
        Objects.requireNonNull(roamingMap);
        Map<K, V> copy = new TreeMap<>();
        copy.putAll(roamingMap);
        String prevRepresentation = copy.toString();
        Set<Map.Entry<K, V>> roamingSetBefore = correctEntrySet(roamingMap);
        String representation = roamingMap.toString();
        Set<Map.Entry<K, V>> roamingSetAfter = correctEntrySet(roamingMap);
        if (!Objects.equals(roamingSetBefore, roamingSetAfter)) {
            throw new RuntimeException("toString method of RoamingMap operated incorrectly");
        }
        if (!Objects.equals(prevRepresentation, representation)) {
            logger.log(Level.WARNING, "toString method of RoamingMap returned incorrect value; correct value was used instead");
            return prevRepresentation;
        }
        return representation;
    }
}
