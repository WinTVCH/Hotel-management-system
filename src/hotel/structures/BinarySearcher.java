package hotel.structures;

import java.util.List;
import java.util.function.ToIntFunction;

public class BinarySearcher<T> {
    private ToIntFunction<T> keyOf;

    public BinarySearcher(ToIntFunction<T> keyOf) {
        this.keyOf = keyOf;
    }

    public int indexOf(List<T> sorted, int key) {
        int low = 0;
        int high = sorted.size() - 1;
        while (low <= high) {
            int middle = (low + high) >>> 1;
            int value = keyOf.applyAsInt(sorted.get(middle));
            if (value == key) {
                return middle;
            }
            if (value < key) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return -1;
    }

    public T find(List<T> sorted, int key) {
        int index = indexOf(sorted, key);
        return index < 0 ? null : sorted.get(index);
    }

}
