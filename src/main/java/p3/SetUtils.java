package p3;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for sets.
 */
public class SetUtils {

    /**
     * Create an immutable copy of the given set.
     * @param set The set to copy.
     * @return An immutable copy of the given set.
     * @param <N> The type of the elements in the set.
     */
    public static <N> Set<N> immutableCopyOf(Set<N> set) {
        final Set<N> result = new HashSet<>(set.size(), 0.9f);
        result.addAll(set);
        return Collections.unmodifiableSet(result);
    }

    /**
     * Create a mutable copy of the given set.
     * @param set The set to copy.
     * @return A mutable copy of the given set.
     * @param <N> The type of the elements in the set.
     */
    public static <N> Set<N> mutableCopyOf(Set<N> set) {
        final Set<N> result = new HashSet<>((int) (set.size() / 0.75f), 0.75f);
        result.addAll(set);
        return result;
    }
}
