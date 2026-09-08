package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.two_sum;

public class TwoSumBruteForceCheck {
    public static void main(String[] args) {
        System.out.println(twoSumeBruteForce(new int[]{4, 2, 6, 1, 9, 5}, 15));
    }

    private static String twoSumeBruteForce(int[] ints, int target) {
        for (int i = 0; i < ints.length; i++) {
            for (int j = i; j < ints.length; j++) {
                if (ints[i] + ints[j] == target) {
                    return i + " " + j;
                }
            }
        }
        return "";
    }
}
