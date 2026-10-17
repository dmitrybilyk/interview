package com.conduct.interview.coding.leetcode._2_sliding_window.longest_substring_no_repeat;

import java.util.HashMap;
import java.util.Map;

public class LongestSubstringNoRepeatCheck3 {
    public static void main(String[] args) {
//        System.out.println(lengthOfLongestSubstringCheck("abcabcbb")); // 3
//        System.out.println(lengthOfLongestSubstringCheck("bbbbb"));    // 1
//        System.out.println(lengthOfLongestSubstringCheck("pwwkew"));   // 3

        System.out.println(longestSubstringNoRepeatCheck("abcabcbb")); // "abc"
        System.out.println(longestSubstringNoRepeatCheck("bbbbb"));    // "b"
        System.out.println(longestSubstringNoRepeatCheck("pwwkew"));   // "wke"
    }

    private static String longestSubstringNoRepeatCheck(String s) {
        int left = 0;
        int bestLeft = 0;
        int bestLen = 0;

        Map<Character, Integer> lastIndex = new HashMap<>();

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
}
