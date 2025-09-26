package com.conduct.interview.coding.strings.one_edit_away;

public class OneEditAwayCheck {
    public static void main(String[] args) {
        String s1 = "asdf";
        String s2 = "asdfb";
        System.out.println(isOneEditAway(s1, s2));
    }

    private static boolean isOneEditAway(String s1, String s2) {
        if (Math.abs(s1.length() - s2.length()) >= 2) {
            return false;
        } else if (s1.length() == s2.length()) {
            return isSameSizeCheck(s1, s2);
        } else if (s1.length() > s2.length()) {
            return isDifferentSizeCheck(s1, s2);
        } else {
            return isDifferentSizeCheck(s2, s1);
        }
    }

    private static boolean isDifferentSizeCheck(String s1, String s2) {
        boolean isDifferent = false;
        int i = 0, j = 0;
        while (i < s1.length() && j < s1.length()) {
            if (s1.charAt(i) != s2.charAt(j)) {
                if (isDifferent) {
                    return false;
                }
                isDifferent = true;
                i++;
            }
            i++;
            j++;
        }
        return true;
    }

    private static boolean isSameSizeCheck(String s1, String s2) {
        boolean different = false;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                if (different) {
                    return false;
                }
                different = true;
            }
        }
        return true;
    }
}
