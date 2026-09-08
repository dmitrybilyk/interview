package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.two_sum;

public class TwoSumTwoPointerSortedCheck {
    public static void main(String[] args) {
        System.out.println(twoSumTwoPointerSortedCheck(new int[]{1, 2, 5, 6, 9}, 15));
    }

    private static String twoSumTwoPointerSortedCheck(int[] ints, int target) {
        int left = 0, right = ints.length - 1;

        for (int i = 0; i < ints.length; i++) {
            int sum = ints[left] + ints[right];
            if (sum == target) {
                return left + " " + right;
            } else if (sum > target) {
                right--;
            } else {
                left++;
            }
        }

        return "";
    }
}
