package com.conduct.interview.coding.leetcode._2_sliding_window.longest_substring_no_repeat;

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
        return longestSubstringNoRepeat(s).length();
    }

    public static String longestSubstringNoRepeat(String s) {
        Map<Character, Integer> lastIndex = new HashMap<>();
        int left = 0;
        int bestLeft = 0, bestLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastIndex.containsKey(c) && lastIndex.get(c) >= left) {
                left = lastIndex.get(c) + 1;
            }
            lastIndex.put(c, right);

            int len = right - left + 1;
            if (len > bestLen) {
                bestLen = len;
                bestLeft = left;
            }
        }
        return s.substring(bestLeft, bestLeft + bestLen);
    }

    public static void main(String[] args) {
        System.out.println(lengthOfLongestSubstring("abcabcbb")); // 3
        System.out.println(lengthOfLongestSubstring("bbbbb"));    // 1
        System.out.println(lengthOfLongestSubstring("pwwkew"));   // 3

        System.out.println(longestSubstringNoRepeat("abcabcbb")); // "abc"
        System.out.println(longestSubstringNoRepeat("bbbbb"));    // "b"
        System.out.println(longestSubstringNoRepeat("pwwkew"));   // "wke"
    }
}
