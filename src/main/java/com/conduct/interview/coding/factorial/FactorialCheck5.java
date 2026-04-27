package com.conduct.interview.coding.factorial;

public class FactorialCheck5 {
    public static void main(String[] args) {
        System.out.println(factorialIterative5(5));
        System.out.println(factorialRecursive5(5));
    }

    private static long factorialRecursive5(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * factorialRecursive5(n - 1);
    }

    private static long factorialIterative5(int n) {
        if (n <= 1) {
            return 1;
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result = result * i;
        }
        return result;
    }
}
