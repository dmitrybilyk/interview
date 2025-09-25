package com.conduct.interview.coding.strings.longest_non_repeatable;

import java.util.Arrays;

public class LongestNonRepeatableCheck2 {
    public static void main(String[] args) {
        String s = "longestnonerepeatable";
        System.out.println(longestNonRepeatable(s));
    }

    private static String longestNonRepeatable(String s) {
        int[] positions = new int[256];

        int left = 0;
        int start = 0;
        int maxLen = 0;
        Arrays.fill(positions, -1);

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (positions[c] >= left) {
                left = positions[c] + 1;
            }

            positions[c] = right;

            if (right - left + 1 > maxLen) {
                maxLen = right - left + 1;
                start = left;
            }

        }
        return s.substring(start, start + maxLen);
    }
}
