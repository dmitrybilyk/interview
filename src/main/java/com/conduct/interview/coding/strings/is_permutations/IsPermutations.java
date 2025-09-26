package com.conduct.interview.coding.strings.is_permutations;

public class IsPermutations {
    public static void main(String[] args) {
        String s1 = "abcd";
        String s2 = "cbda";
//Can sort char arrays and just compare with Arrays.equals or:
        System.out.println(areStringsPermutations(s1, s2));
    }

    private static boolean areStringsPermutations(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        if (s1.length() != s2.length()) {
            return false;
        }

        char[] counts = new char[256];

        for(int c = 0; c < s1.length(); c++) {
            counts[c]++;
        }

        for (int c = 0; c < s2.length(); c++) {
            counts[c]--;
            if (counts[c] < 0) {
                return false;
            }
        }
        return true;
    }
}
