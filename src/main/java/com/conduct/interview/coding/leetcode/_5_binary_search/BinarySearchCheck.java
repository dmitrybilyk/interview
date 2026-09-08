package com.conduct.interview.coding.leetcode._5_binary_search;

// Чернетка / самоперевірка звичайного бінарного пошуку (порівняй з BinarySearch.java).
public class BinarySearchCheck {
    public static void main(String[] args) {
        System.out.println(binarySearchCheck(new int[]{2, 4, 6, 7, 9, 12, 45, 78}, 45));
    }

    private static int binarySearchCheck(int[] array, int target) {
        int low = 0, high = array.length - 1;
        int mid = high - (high - low) / 2;
        // УВАГА: mid рахується лише ОДИН РАЗ тут, до циклу.

        while (low <= high) {
            if (array[mid] == target) {
                return mid;
            } else if (array[mid] > target) {
                high = mid - 1;
            } else {
                low = mid + 1;
                // low/high змінюються, але mid після цього НЕ перераховується —
                // на наступних ітераціях перевіряється те саме застаріле значення mid.
                // Це баг: правильний бінарний пошук має обчислювати mid
                // ЗАНОВО на кожній ітерації циклу (як у BinarySearch.java).
            }
        }
        return -1;
    }
}
