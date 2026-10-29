package com.conduct.interview.coding.leetcode._5_binary_search;

// Чернетка / самоперевірка звичайного бінарного пошуку (порівняй з BinarySearch.java).
public class BinarySearchCheck2 {
    public static void main(String[] args) {
        System.out.println(binarySearchCheck(new int[]{2, 4, 6, 7, 9, 12, 45, 78}, 45));
    }

    private static int binarySearchCheck(int[] array, int target) {
        int lo = 0, hi = array.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (array[mid] == target) {
                return mid;
            } else if (array[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return -1;
    }
}
