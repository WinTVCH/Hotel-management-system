package hotel.structures;

import hotel.housekeeping.HousekeepingTask;
import java.util.ArrayList;
import java.util.List;

public class TaskHeap {
    private HousekeepingTask[] heap = new HousekeepingTask[16];
    private int size;

    public void push(HousekeepingTask task) {
        if (size == heap.length) {
            HousekeepingTask[] bigger = new HousekeepingTask[heap.length * 2];
            System.arraycopy(heap, 0, bigger, 0, size);
            heap = bigger;
        }
        heap[size] = task;
        siftUp(size);
        size++;
    }

    public HousekeepingTask peek() {
        return size == 0 ? null : heap[0];
    }

    public HousekeepingTask pop() {
        if (size == 0) {
            return null;
        }
        HousekeepingTask top = heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        siftDown(0);
        return top;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public List<HousekeepingTask> toOrderedList() {
        TaskHeap copy = new TaskHeap();
        for (int i = 0; i < size; i++) {
            copy.push(heap[i]);
        }
        List<HousekeepingTask> result = new ArrayList<>();
        while (!copy.isEmpty()) {
            result.add(copy.pop());
        }
        return result;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap[index].compareTo(heap[parent]) <= 0) {
                return;
            }
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = index * 2 + 1;
            int right = left + 1;
            int largest = index;
            if (left < size && heap[left].compareTo(heap[largest]) > 0) {
                largest = left;
            }
            if (right < size && heap[right].compareTo(heap[largest]) > 0) {
                largest = right;
            }
            if (largest == index) {
                return;
            }
            swap(index, largest);
            index = largest;
        }
    }

    private void swap(int a, int b) {
        HousekeepingTask temp = heap[a];
        heap[a] = heap[b];
        heap[b] = temp;
    }

}
