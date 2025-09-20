package com.conduct.interview.coding;

public class Fibonacci {

    public static int fibonacci(int n) {
        if (n <= 0) return 0;      // base case for 0 or negative
        if (n == 1) return 1;      // base case for 1
        return fibonacci(n - 1) + fibonacci(n - 2);  // recursive step
    }

    public static void main(String[] args) {
        int n = 10;
        for (int i = 0; i < n; i++) {
            System.out.print(fibonacci(i) + " ");
        }
    }
}
