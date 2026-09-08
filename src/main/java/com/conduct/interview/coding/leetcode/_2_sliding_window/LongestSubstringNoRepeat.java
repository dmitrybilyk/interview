package com.conduct.interview.coding.leetcode._2_sliding_window;

import java.util.HashMap;
import java.util.Map;

// LC 3 — Longest Substring Without Repeating Characters
// Pattern: Sliding window with a map tracking last-seen index
// Time O(n), Space O(min(n,charset))

// Задача: знайти довжину найдовшого підрядка без повторюваних символів.
// Ідея "ковзного вікна" [left..right]: right рухається вперед завжди,
// а left "стрибає" вправо тільки коли зустрічаємо символ, що вже є у вікні.
public class LongestSubstringNoRepeat {

    public static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastIndex = new HashMap<>();
        // символ -> індекс, де він востаннє зустрічався

        int max = 0, left = 0;
        // left — лівий край поточного вікна без повторів

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastIndex.containsKey(c) && lastIndex.get(c) >= left) {
                left = lastIndex.get(c) + 1; // jump left past the duplicate
                // символ вже є всередині поточного вікна -> стискаємо вікно,
                // пересуваючи left одразу за місце попереднього входження
            }
            lastIndex.put(c, right);
            // запам'ятовуємо (або оновлюємо) останню позицію цього символу

            max = Math.max(max, right - left + 1);
            // довжина поточного вікна = right - left + 1, оновлюємо максимум
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println(lengthOfLongestSubstring("abcabcbb")); // 3
        System.out.println(lengthOfLongestSubstring("bbbbb"));    // 1
        System.out.println(lengthOfLongestSubstring("pwwkew"));   // 3
    }
}
