package com.conduct.interview.coding.fibonacci;

public class FactorialCheck2 {
    public static void main(String[] args) {
//        System.out.println(factorialIterative(5));
        System.out.println(factorialRecursive(5));
    }

    public static int factorialIterative(int n) {
        if (n <= 1) {
            return 1;
        }
        int result = 1;
        for(int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static int factorialRecursive(int n) {
        if (n == 0) {
            return 1;
        }
        return n * factorialRecursive(n - 1);
    }
}
