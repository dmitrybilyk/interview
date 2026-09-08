package com.conduct.interview.coding.leetcode._5_binary_search;

// Чернетка / самоперевірка пошуку в оберненому (rotated) масиві.
// Порівняй з правильною версією в SearchRotatedArray.java — тут є два баги, див. нижче.
public class SearchRotatedArrayCheck {
    public static void main(String[] args) {
        System.out.println(searchRotatedArray(new int[]{5, 6, 7, 8, 1, 2, 3, 4}, 3));
    }

    private static int searchRotatedArray(int[] ints, int target) {
        int lo = 0;
        int hi = ints.length - 1;
        int mid = lo + (hi - lo) /2;
        // БАГ 1: так само як у BinarySearchCheck — mid обчислюється лише один раз,
        // до циклу, і більше ніколи не перераховується після зміни lo/hi.

        while (lo <= hi) {
            if (ints[mid] == target) return target;
            // БАГ 2: тут має бути "return mid;" (індекс), а не "return target;"
            // (значення, яке ми й так знаємо — його передали як параметр).

            if (ints[lo] <= ints[hi]) {
                // це умова "весь підмасив [lo..hi] відсортований без розриву",
                // а не "ліва половина відсортована" як у SearchRotatedArray.java

                if (ints[lo] <= target && target <= ints[mid]) {
                    hi = mid - 1;
                } else {
                    lo = mid + 1;
                }
            } else {
                if (ints[mid] <= target && target <= ints[hi]) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
        }
        return -1;
    }
}
