package com.conduct.interview.coding.leetcode._2_sliding_window.longest_substring_no_repeat;

import java.util.HashMap;
import java.util.Map;

public class LongestSubstringNoRepeatCheck {
    public static void main(String[] args) {
//        System.out.println(lengthOfLongestSubstringCheck("abcabcbb")); // 3
//        System.out.println(lengthOfLongestSubstringCheck("bbbbb"));    // 1
//        System.out.println(lengthOfLongestSubstringCheck("pwwkew"));   // 3

        System.out.println(longestSubstringNoRepeatCheck("abcabcbb")); // "abc"
        System.out.println(longestSubstringNoRepeatCheck("bbbbb"));    // "b"
        System.out.println(longestSubstringNoRepeatCheck("pwwkew"));   // "wke"
    }

    private static String longestSubstringNoRepeatCheck(String s) {
        Map<Character, Integer> map = new HashMap<>();

        int left = 0, bestLen = 0, bestLeft = 0;

        for(int right = 0; right < s.length(); right++) {
            char key = s.charAt(right);
            if (map.containsKey(key) && map.get(key) >= left) {
                left = map.get(key) + 1;
            }
            map.put(key, right);
            int len = right - left + 1;

            if (len > bestLen) {
                bestLen = len;
                bestLeft = left;
            }

        }
        return s.substring(bestLeft, bestLeft + bestLen);
    }
}
