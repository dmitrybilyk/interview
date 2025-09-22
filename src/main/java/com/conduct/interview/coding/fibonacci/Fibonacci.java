package com.conduct.interview.coding.fibonacci;

public class Fibonacci {

    public static void main(String[] args) {
        System.out.print(fibonacciRecursive(10) + " ");
//        System.out.print(fibonacciFormula(10) + " ");
//        System.out.print(fibonacciIterative(10) + " ");
    }

    public static int fibonacciRecursive(int n) {
        if (n <= 0) return 0;      // base case for 0 or negative
        if (n == 1) return 1;      // base case for 1
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);  // recursive step
    }

    public static long fibonacciIterative(int n) {
        if (n <= 1) return n;

        long prev = 0; // F(0)
        long curr = 1; // F(1)

        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }

        return curr;
    }

    public static long fibonacciFormula(int n) {
        double sqrt5 = Math.sqrt(5);
        double phi = (1 + sqrt5) / 2;
        double psi = (1 - sqrt5) / 2;

        return Math.round((Math.pow(phi, n) - Math.pow(psi, n)) / sqrt5);
    }

}
