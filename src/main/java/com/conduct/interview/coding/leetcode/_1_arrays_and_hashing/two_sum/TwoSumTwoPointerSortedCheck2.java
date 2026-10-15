package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.two_sum;

public class TwoSumTwoPointerSortedCheck2 {
    public static void main(String[] args) {
        System.out.println(twoSumTwoPointerSortedCheck(new int[]{1, 2, 5, 6, 9}, 15));
    }

    private static String twoSumTwoPointerSortedCheck(int[] ints, int target) {
        int left = 0;
        int right = ints.length - 1;
        while (left < right) {
            int sum = ints[left] + ints[right];
            if (sum == target) {
                return left + ": " + right;
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return "";
    }
}
