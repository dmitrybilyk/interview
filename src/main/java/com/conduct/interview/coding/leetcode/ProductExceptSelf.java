package com.conduct.interview.coding.leetcode;

import java.util.Arrays;

// LC 238 — Product of Array Except Self
// Pattern: Prefix product left pass, then suffix product right pass — no division
// Time O(n), Space O(1) output array excluded
public class ProductExceptSelf {

    public static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        result[0] = 1;
        for (int i = 1; i < n; i++) result[i] = result[i - 1] * nums[i - 1]; // prefix
        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= suffix;
            suffix *= nums[i]; // accumulate suffix on the fly
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(productExceptSelf(new int[]{1, 2, 3, 4}))); // [24,12,8,6]
    }
}
