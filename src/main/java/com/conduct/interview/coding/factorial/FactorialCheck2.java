package com.conduct.interview.coding.factorial;

public class FactorialCheck2 {
    public static void main(String[] args) {
//        System.out.println(factorialIterative(10));
        System.out.println(factorialRecursive(10));
    }

    public static int factorialIterative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("wrong argument");
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

    public static int factorialRecursive(int n) {
        if (n <= 1) {
            return 1;
        }
        return factorialIterative(n - 1) + factorialIterative(n - 2);
    }
}
