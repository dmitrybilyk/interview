package com.conduct.interview.coding.leetcode._5_binary_search;

// LC 704 — Binary Search
// Pattern: Halve search space each step; watch for lo <= hi and mid calculation
// Time O(log n), Space O(1)

// Задача: знайти індекс target у ВІДСОРТОВАНОМУ масиві.
// Ідея: на кожному кроці дивимось на середній елемент і відкидаємо
// половину масиву, де target точно бути не може.
public class BinarySearch {

    public static int search(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        // межі поточного вікна пошуку

        while (lo <= hi) {
            // поки вікно не порожнє — mid обов'язково перераховуємо на кожній ітерації!

            int mid = lo + (hi - lo) / 2; // avoids int overflow vs (lo+hi)/2
            // середина поточного вікна; формула захищає від переповнення int

            if      (nums[mid] == target) return mid;
            // знайшли елемент — повертаємо його індекс

            else if (nums[mid] < target)  lo = mid + 1;
            // середина замала -> шукане може бути тільки правіше -> звужуємо вікно вправо

            else                          hi = mid - 1;
            // середина завелика -> шукане може бути тільки лівіше -> звужуємо вікно вліво
        }
        return -1;
        // вікно спорожніло — елемента в масиві немає
    }

    public static void main(String[] args) {
        System.out.println(search(new int[]{-1, 0, 3, 5, 9, 12}, 9));  // 4
        System.out.println(search(new int[]{-1, 0, 3, 5, 9, 12}, 2));  // -1
    }
}
