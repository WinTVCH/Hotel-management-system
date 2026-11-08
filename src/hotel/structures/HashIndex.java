package hotel.structures;

import java.util.ArrayList;
import java.util.List;

public class HashIndex<K, V> {
    private Entry<K, V>[] buckets;
    private int size;

    private static class Entry<K, V> {
        private K key;
        private V value;
        private Entry<K, V> next;

        private Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    public HashIndex() {
        buckets = (Entry<K, V>[]) new Entry[16];
    }

    private int indexFor(Object key, int length) {
        return (key.hashCode() & 0x7fffffff) % length;
    }

    public void put(K key, V value) {
        int index = indexFor(key, buckets.length);
        for (Entry<K, V> e = buckets[index]; e != null; e = e.next) {
            if (e.key.equals(key)) {
                e.value = value;
                return;
            }
        }
        buckets[index] = new Entry<>(key, value, buckets[index]);
        size++;
        if (size > buckets.length * 3 / 4) {
            resize();
        }
    }

    public V get(K key) {
        if (key == null) {
            return null;
        }
        for (Entry<K, V> e = buckets[indexFor(key, buckets.length)]; e != null; e = e.next) {
            if (e.key.equals(key)) {
                return e.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V remove(K key) {
        int index = indexFor(key, buckets.length);
        Entry<K, V> previous = null;
        for (Entry<K, V> e = buckets[index]; e != null; e = e.next) {
            if (e.key.equals(key)) {
                if (previous == null) {
                    buckets[index] = e.next;
                } else {
                    previous.next = e.next;
                }
                size--;
                return e.value;
            }
            previous = e;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public List<V> values() {
        List<V> result = new ArrayList<>();
        for (Entry<K, V> bucket : buckets) {
            for (Entry<K, V> e = bucket; e != null; e = e.next) {
                result.add(e.value);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] old = buckets;
        buckets = (Entry<K, V>[]) new Entry[old.length * 2];
        for (Entry<K, V> bucket : old) {
            for (Entry<K, V> e = bucket; e != null; e = e.next) {
                int index = indexFor(e.key, buckets.length);
                buckets[index] = new Entry<>(e.key, e.value, buckets[index]);
            }
        }
    }

}
