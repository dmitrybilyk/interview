package com.conduct.interview.coding.fibonacci;

public class FibonacciCheck5 {
    public static void main(String[] args) {
        System.out.println(fibonacciIterative5(5));
        System.out.println(fibonacciRecursive5(5));
    }

    private static long fibonacciRecursive5(int n) {
        if (n <= 1) {
            return 1;
        }
        return fibonacciIterative5(n - 1) + fibonacciIterative5(n - 2);
    }

    private static int fibonacciIterative5(int n) {
        if (n <= 1) {
            return 1;
        }
        int prev = 0;
        int curr = 1;
        for (int i = 2; i <= n; i++) {
            int next = prev + curr;
            prev = curr;
            curr =  next;
        }
        return curr;
    }
}
