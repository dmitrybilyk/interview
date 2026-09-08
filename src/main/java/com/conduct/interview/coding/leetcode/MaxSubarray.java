package com.conduct.interview.coding.leetcode;

// LC 53 — Maximum Subarray (Kadane's Algorithm)
// Pattern: Track running sum; reset to 0 when it goes negative
// Time O(n), Space O(1)
public class MaxSubarray {

    public static int maxSubArray(int[] nums) {
        int max = nums[0], current = nums[0];
        for (int i = 1; i < nums.length; i++) {
            current = Math.max(nums[i], current + nums[i]);
            max = Math.max(max, current);
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println(maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4})); // 6
        System.out.println(maxSubArray(new int[]{1}));                               // 1
        System.out.println(maxSubArray(new int[]{-1, -2}));                          // -1
    }
}
