package com.conduct.interview.coding.strings.is_permutations;

public class IsPermutationsCheck {
    public static void main(String[] args) {
        String s1 = "asdf";
        String s2 = "fsda";
        System.out.println(isPermutations(s1, s2));
    }

    private static boolean isPermutations(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        }
        int[] counts = new int[256];

        for (int i = 0; i < s1.length(); i++) {
            counts[s1.charAt(i)]++;
        }
        for (int i = 0; i < s2.length(); i++) {
            counts[s2.charAt(i)]++;
        }

        for (int i = 0; i < s1.length(); i++) {
            if (counts[s1.charAt(i)] != 2) {
                return false;
            }
        }
        return true;
    }
}
