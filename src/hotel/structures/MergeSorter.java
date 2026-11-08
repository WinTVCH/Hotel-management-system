package hotel.structures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MergeSorter<T> {
    private Comparator<T> comparator;

    public MergeSorter(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public List<T> sort(List<T> items) {
        if (items.size() <= 1) {
            return new ArrayList<>(items);
        }
        int middle = items.size() / 2;
        List<T> left = sort(items.subList(0, middle));
        List<T> right = sort(items.subList(middle, items.size()));
        return merge(left, right);
    }

    private List<T> merge(List<T> left, List<T> right) {
        List<T> result = new ArrayList<>(left.size() + right.size());
        int i = 0;
        int j = 0;
        while (i < left.size() && j < right.size()) {
            if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size()) {
            result.add(left.get(i++));
        }
        while (j < right.size()) {
            result.add(right.get(j++));
        }
        return result;
    }

}
