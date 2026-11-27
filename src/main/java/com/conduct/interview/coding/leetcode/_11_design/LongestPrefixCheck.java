package com.conduct.interview.coding.leetcode._11_design;

// LC 14 — Longest Common Prefix
// Задача: знайти найдовший спільний префікс серед усіх рядків у масиві.
// Ідея: беремо перший рядок як "кандидат" на префікс і поступово вкорочуємо його
// справа, доки він не стане справжнім префіксом кожного наступного рядка.
// Час O(n * m), де m — довжина найкоротшого рядка; Пам'ять O(1)
public class LongestPrefixCheck {
    public static void main(String[] args) {
        String[] input = new String[] {"flower", "flow", "flight"};
        System.out.println(findLongestPrefix(input));
    }

    private static String findLongestPrefix(String[] input) {
        if (input.length == 0) {
            return "";
        }

        String prefix = input[0];

        for (int i = 1; i < input.length; i++) {
            if (!input[i].startsWith(prefix)) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
        }

        return prefix;
    }
}
