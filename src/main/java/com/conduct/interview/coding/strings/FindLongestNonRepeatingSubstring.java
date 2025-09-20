package com.conduct.interview.coding.strings;

import java.util.Arrays;

public class FindLongestNonRepeatingSubstring {
    public static void main(String[] args) {
        System.out.println(findSubstring("abgedfgte"));
    }

    private static String findSubstring(String s) {
        int[] lastSeen = new int[256];
        Arrays.fill(lastSeen, -1);

        int left = 0, maxLen = 0, start = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);

            if (lastSeen[c] >= left) {
                left = lastSeen[c] + 1; // move left to avoid duplicate
            }

            lastSeen[c] = right;

            if (right - left + 1 > maxLen) {
                maxLen = right - left + 1;
                start = left;
            }
        }

        return s.substring(start, start + maxLen);
    }
}
