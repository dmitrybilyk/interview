package com.conduct.interview.coding.fibonacci;

import java.util.HashMap;
import java.util.Map;

public class FibonacciCheck4 {
    private static Map<Integer, Long> memo = new HashMap<>();
    public static void main(String[] args) {
        System.out.println(fibonacciIterative4(5));
        System.out.println(fibonacciRecursive4(5));
    }

    private static long fibonacciRecursive4(int n) {
        if (n <= 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }

        if (memo.containsKey(n)) {
            return memo.get(n);
        }
        return fibonacciRecursive4(n - 1) + fibonacciRecursive4(n - 2);
    }

    private static int fibonacciIterative4(int n) {
        if (n <= 1) {
            return n;
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
}
