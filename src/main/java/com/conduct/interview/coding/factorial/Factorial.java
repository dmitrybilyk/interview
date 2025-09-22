package com.conduct.interview.coding.factorial;

public class Factorial {
    public static void main(String[] args) {
//        System.out.println(factorialRecursive(5));
        System.out.println(factorialIterative(5));
    }

    private static int factorialRecursive(int key) {
        if (key == 1) {
            return 1;
        }
        return key * factorialRecursive(key - 1);
    }

    public static long factorialIterative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }

        long result = 1;

        for (int i = 1; i <= n; i++) {
            result *= i; // multiply step by step
        }

        return result;
    }
}
