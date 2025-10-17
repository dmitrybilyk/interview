package com.conduct.interview.coding.heaps;

import java.util.ArrayList;
import java.util.List;

public class MinHeap {
    public static void main(String[] args) {
        MinHeap minHeap = new MinHeap();
        minHeap.insert(5);
        minHeap.insert(2);
        minHeap.insert(8);
        minHeap.insert(1);

        System.out.println(minHeap.extractMin()); // 1
        System.out.println(minHeap.extractMin()); // 2
        System.out.println(minHeap.extractMin()); // 5
        System.out.println(minHeap.extractMin()); // 8

        MaxHeap maxHeap = new MaxHeap();
        maxHeap.insert(5);
        maxHeap.insert(2);
        maxHeap.insert(8);
        maxHeap.insert(1);

        System.out.println(maxHeap.extractMax()); // 8
        System.out.println(maxHeap.extractMax()); // 5
        System.out.println(maxHeap.extractMax()); // 2
        System.out.println(maxHeap.extractMax()); // 1
    }

    private List<Integer> data = new ArrayList<>();

    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i) { return 2 * i + 1; }
    private int right(int i) { return 2 * i + 2; }

    public void insert(int value) {
        data.add(value);
        bubbleUp(data.size() - 1);
    }

    public Integer extractMin() {
        if (data.isEmpty()) return null;

        int root = data.get(0);
        int last = data.remove(data.size() - 1);

        if (!data.isEmpty()) {
            data.set(0, last);
            bubbleDown(0);
        }

        return root;
    }

    private void bubbleUp(int i) {
        int index = i;
        while (index > 0 && data.get(index) < data.get(parent(index))) {
            swap(index, parent(index));
            index = parent(index);
        }
    }

    private void bubbleDown(int i) {
        int index = i;
        while (true) {
            int leftIdx = left(index);
            int rightIdx = right(index);
            int smallest = index;

            if (leftIdx < data.size() && data.get(leftIdx) < data.get(smallest))
                smallest = leftIdx;
            if (rightIdx < data.size() && data.get(rightIdx) < data.get(smallest))
                smallest = rightIdx;
            if (smallest == index) break;
            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        int temp = data.get(i);
        data.set(i, data.get(j));
        data.set(j, temp);
    }

    public Integer peek() {
        return data.isEmpty() ? null : data.get(0);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}



class MaxHeap {
    private List<Integer> data = new ArrayList<>();

    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i) { return 2 * i + 1; }
    private int right(int i) { return 2 * i + 2; }

    public void insert(int value) {
        data.add(value);
        bubbleUp(data.size() - 1);
    }

    public Integer extractMax() {
        if (data.isEmpty()) return null;

        int root = data.get(0);
        int last = data.remove(data.size() - 1);

        if (!data.isEmpty()) {
            data.set(0, last);
            bubbleDown(0);
        }

        return root;
    }

    private void bubbleUp(int i) {
        int index = i;
        while (index > 0 && data.get(index) > data.get(parent(index))) {
            swap(index, parent(index));
            index = parent(index);
        }
    }

    private void bubbleDown(int i) {
        int index = i;
        while (true) {
            int leftIdx = left(index);
            int rightIdx = right(index);
            int largest = index;

            if (leftIdx < data.size() && data.get(leftIdx) > data.get(largest))
                largest = leftIdx;
            if (rightIdx < data.size() && data.get(rightIdx) > data.get(largest))
                largest = rightIdx;
            if (largest == index) break;
            swap(index, largest);
            index = largest;
        }
    }

    private void swap(int i, int j) {
        int temp = data.get(i);
        data.set(i, data.get(j));
        data.set(j, temp);
    }

    public Integer peek() {
        return data.isEmpty() ? null : data.get(0);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}

