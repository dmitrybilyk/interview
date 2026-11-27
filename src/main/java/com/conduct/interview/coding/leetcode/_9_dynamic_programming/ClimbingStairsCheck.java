package com.conduct.interview.coding.leetcode._9_dynamic_programming;

public class ClimbingStairsCheck {
    public static void main(String[] args) {
        System.out.println(climbingWaysCount(5));
    }

    private static int climbingWaysCount(int n) {
        if (n <= 2) {
            return n;
        }

        int a = 1, b = 2;

        for (int i = 3; i <= n; i++) {
            int c = a + b;

            a = b;
            b = c;
        }

        return b;
    }
}
