package com.conduct.interview._20_algorythms_and_data_structures;

public class Check {
    public static void main(String[] args) {
        System.out.println(countFactorial(3));
        System.out.println(countFactorialWithRecursion(3));

        System.out.println(countFibonacci(5));
        System.out.println(fibonacciRecursive(5));
    }

    private static long countFibonacci(int n) {

        if (n <= 1) return n;
        int current = 0, previous = 1;
        for (int i = 0; i <= n; i++) {
            int next = current + previous;
            previous  = current;
            current = next;
        }
        return current;
    }

    public static long fibonacciRecursive(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Fibonacci is not defined for negative numbers");
        }
        if (n <= 1) {
            return n;
        }
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }

    private static long countFactorialWithRecursion(int n) {
        if (n == 0 || n == 1) return 1;

        return n * countFactorialWithRecursion(n - 1);
    }

    private static long countFactorial(int n) {
        if (n <= 1) return 1;
        long result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }


}
