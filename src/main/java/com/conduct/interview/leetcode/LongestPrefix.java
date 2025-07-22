package com.conduct.interview.leetcode;

public class LongestPrefix {
    public static void main(String[] args) {
        String[] input = new String[] {"flower", "flow", "flight"};
        System.out.println(findLongestPrefix(input));
    }

    private static String findLongestPrefix(String[] input) {
        if (input == null || input.length == 0) return "";

        String prefix = input[0];
        for (int i = 1; i < input.length; i++) {
            while(!input[i].startsWith(prefix)) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
        }
        return prefix;
    }
}
