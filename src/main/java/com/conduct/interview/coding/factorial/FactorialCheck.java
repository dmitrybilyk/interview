package com.conduct.interview.coding.factorial;

public class FactorialCheck {
    public static void main(String[] args) {
//        System.out.println(factorialIterative(5));
        System.out.println(factorialRecursive(5));
    }

    public static int factorialIterative(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Can't calcualte factorial of " + n);
        }

        int result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static int factorialRecursive(int n) {
        if (n == 1) {
            return 1;
        }
        return n * factorialIterative(n - 1);
    }
}
