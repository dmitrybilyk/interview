package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.product_except_self;

public class ProductExceptSelfCheck4 {
    public static void main(String[] args) {
        System.out.println(productExceptSelf4(new int[] {3, 5, 7, 8}));
    }

    private static int[] productExceptSelf4(int[] ints) {
        int n = ints.length;
        int[] result = new int[n];

        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * ints[i - 1];
        }

        int suffix = 1;

        for (int i = n - 1; i >= 0; i--) {
            result[i] = result[i] * suffix;
            suffix = suffix * ints[i];
        }

        return result;
    }
}
