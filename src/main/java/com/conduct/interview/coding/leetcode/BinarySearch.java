package com.conduct.interview.coding.leetcode;

// LC 704 — Binary Search
// Pattern: Halve search space each step; watch for lo <= hi and mid calculation
// Time O(log n), Space O(1)
public class BinarySearch {

    public static int search(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2; // avoids int overflow vs (lo+hi)/2
            if      (nums[mid] == target) return mid;
            else if (nums[mid] < target)  lo = mid + 1;
            else                          hi = mid - 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(search(new int[]{-1, 0, 3, 5, 9, 12}, 9));  // 4
        System.out.println(search(new int[]{-1, 0, 3, 5, 9, 12}, 2));  // -1
    }
}
