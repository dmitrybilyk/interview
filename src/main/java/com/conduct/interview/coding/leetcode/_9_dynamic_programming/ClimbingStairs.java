package com.conduct.interview.coding.leetcode._9_dynamic_programming;

// LC 70 — Climbing Stairs
// Pattern: DP — each step = ways(n-1) + ways(n-2), identical to Fibonacci
// Time O(n), Space O(1)

// Задача: скількома способами можна піднятись на n сходинок, якщо за раз
// можна робити крок на 1 або на 2 сходинки.
// Ідея: щоб дістатись сходинки n, останній крок був або з (n-1), або з (n-2).
// Тому ways(n) = ways(n-1) + ways(n-2) — точнісінько як у Фібоначчі.
public class ClimbingStairs {

    public static int climbStairs(int n) {
        if (n <= 2) {
            return n;
            // для 1 сходинки — 1 спосіб, для 2 сходинок — 2 способи (базові випадки)
        }

        int a = 1, b = 2;
        // a = ways(n-2), b = ways(n-1) на поточному кроці; тримаємо тільки останні два значення

        for (int i = 3; i <= n; i++) {
            int c = a + b;
            // ways(i) = ways(i-2) + ways(i-1)

            a = b;
            b = c;
            // зсуваємо "вікно" на один крок вперед, як у обчисленні Фібоначчі без масиву
        }
        return b;
    }

    public static void main(String[] args) {
        System.out.println(climbStairs(2)); // 2
        System.out.println(climbStairs(5)); // 8
    }
}
