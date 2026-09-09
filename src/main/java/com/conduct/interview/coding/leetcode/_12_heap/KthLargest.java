package com.conduct.interview.coding.leetcode._12_heap;

import java.util.PriorityQueue;

// LC 215 — Kth Largest Element in an Array
// Pattern: Min-heap of size k — root is always the kth largest seen so far
// Time O(n log k), Space O(k)

// Задача: знайти k-й за величиною елемент у масиві (без сортування всього масиву).
// Ідея: тримаємо min-heap розміром рівно k.
// Він зберігає k найбільших елементів; найменший з них (корінь) — і є k-й найбільший загалом.
// Якщо черга переповнюється (більше k) — викидаємо найменший: він точно не k-й найбільший.
public class KthLargest {

    public static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        // PriorityQueue в Java за замовчуванням — min-heap (корінь = найменший елемент)

        for (int n : nums) {
            minHeap.offer(n);
            // додаємо елемент у купу

            if (minHeap.size() > k) {
                minHeap.poll();
                // купа стала більша за k -> виштовхуємо найменший елемент;
                // він точно не входить до k найбільших
            }
        }

        return minHeap.peek();
        // після обходу всього масиву корінь купи — найменший серед k найбільших,
        // тобто рівно k-й найбільший елемент
    }

    public static void main(String[] args) {
        System.out.println(findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2));         // 5
        System.out.println(findKthLargest(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4)); // 4
    }
}
