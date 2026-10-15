package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.product_except_self;

public class ProductExceptSelfCheck5 {
    public static void main(String[] args) {
        System.out.println(productExceptSelf5(new int[] {3, 5, 7, 8}));
    }

    private static int[] productExceptSelf5(int[] ints) {
        int length = ints.length;
        int[] result = new int[length];

        result[0] = 1;

        for (int i = 1; i < length; i++) {
            result[i] = result[i - 1] * ints[i - 1];
        }

        int suffix = 1;
        for (int i = length - 1; i >= 0; i--) {
            result[i] = result[i] * suffix;
            suffix = suffix * ints[i];
        }

        return result;
    }
}
