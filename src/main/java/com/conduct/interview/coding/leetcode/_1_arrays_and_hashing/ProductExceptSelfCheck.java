package com.conduct.interview.coding.leetcode._1_arrays_and_hashing;

import java.util.Arrays;

public class ProductExceptSelfCheck {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(productExceptSelfCheck(new int[]{1, 2, 3, 4}))); // [24,12,8,6]
    }

    private static long[] productExceptSelfCheck(int[] ints) {
        long[] result = new long[ints.length];

        result[0] = 1;

        for (int i = 1; i < ints.length; i++) {
            result[i] = result[i - 1] * ints[i - 1];
        }

        int suffix = 1;

        for (int i = ints.length - 1; i >= 0; i--) {
            result[i] = result[i] * suffix;
            suffix = suffix * ints[i];
        }

        return result;
    }
}
