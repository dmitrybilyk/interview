package com.conduct.interview.coding.fibonacci;

public class FibonacciCheck {
    public static void main(String[] args) {
//        System.out.println(fibonacciIterative(10));
        System.out.println(fibonacciRecursive(10));
    }

    public static int fibonacciIterative(int n) {
        if (n <= 1) {
            return 1;
        }
        int prev = 0;
        int curr = 1;

        for (int i = 2; i <= n; i++) {
            int next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    public static int fibonacciRecursive(int n) {
        if (n == 0) {
            return 0;
        }
        if (n <= 1) {
            return 1;
        }
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }
}
