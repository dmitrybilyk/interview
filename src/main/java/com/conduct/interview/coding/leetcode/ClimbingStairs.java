package com.conduct.interview.coding.leetcode;

// LC 70 — Climbing Stairs
// Pattern: DP — each step = ways(n-1) + ways(n-2), identical to Fibonacci
// Time O(n), Space O(1)
public class ClimbingStairs {

    public static int climbStairs(int n) {
        if (n <= 2) return n;
        int a = 1, b = 2;
        for (int i = 3; i <= n; i++) {
            int c = a + b;
            a = b;
            b = c;
        }
        return b;
    }

    public static void main(String[] args) {
        System.out.println(climbStairs(2)); // 2
        System.out.println(climbStairs(5)); // 8
    }
}
