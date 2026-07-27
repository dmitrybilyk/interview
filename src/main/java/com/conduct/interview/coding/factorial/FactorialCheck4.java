package com.conduct.interview.coding.factorial;

public class FactorialCheck4 {
    public static void main(String[] args) {
        System.out.println(factorialIterative4(3));
    }

    private static long factorialIterative4(int n) {
        long result = 1;

        if (n == 0) {
            result = 1;
        }

        for (int i = 2; i < n; i++) {
            result = result * n;
        }

        return result;
    }
}
