package com.conduct.interview.coding.fibonacci;

public class FibonacciCheck3 {
    public static void main(String[] args) {
        System.out.println(fibonacc3(10));
//        System.out.println(fibonacciRecursive3(5));
    }

    private static int fibonacc3(int n) {
        if (n <= 1) {
            return n;
        }
        int prev = 0;
        int curr = 1;
        for(int i = 2; i <= n; i++) {
            int next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    private static boolean fibonacci3(int i) {
        return false;
    }
}
