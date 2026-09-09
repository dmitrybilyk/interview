package com.conduct.interview.coding.leetcode._1_arrays_and_hashing;

import java.util.Arrays;

public class ProductExceptSelfWithDivisionCheck {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(productExceptSelfWithDivision(new int[]{1, 2, 3, 4}))); // [24,12,8,6]

    }

    private static long[] productExceptSelfWithDivision(int[] ints) {
        long[] result = new long[ints.length];
        long product = 1;
        for (int i  = 0; i < ints.length; i++) {
            product *= ints[i];
        }
        for (int i  = 0; i < ints.length; i++) {
            result[i] = product / ints[i];
        }
        return result;
    }
}
