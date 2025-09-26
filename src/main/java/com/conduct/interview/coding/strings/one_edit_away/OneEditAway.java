package com.conduct.interview.coding.strings.one_edit_away;

public class OneEditAway {
    public static void main(String[] args) {
        System.out.println(isOneEditAway("asdf", "asdfb")); // true (insert)
        System.out.println(isOneEditAway("pale", "bale"));  // true (replace)
        System.out.println(isOneEditAway("pale", "ple"));   // true (delete)
        System.out.println(isOneEditAway("pale", "bake"));  // false (two edits)
    }

    private static boolean isOneEditAway(String s1, String s2) {
        if (Math.abs(s1.length() - s2.length()) >= 2) {
            return false;
        } else if (s1.length() == s2.length()) {
            return checkReplaceCase(s1, s2);
        } else if (s1.length() > s2.length()) {
            return checkInsertDeleteCase(s1, s2);
        } else {
            return checkInsertDeleteCase(s2, s1);
        }
    }

    private static boolean checkInsertDeleteCase(String bigger, String smaller) {
        int i = 0;
        int j = 0;
        boolean foundDifference = false;

        while (i < bigger.length() && j < smaller.length()) {
            if (bigger.charAt(i) !=  smaller.charAt(j)) {
                if (foundDifference) {
                    return false;
                }
                foundDifference = true;
                i++;
            } else {
                i++;
                j++;
            }
        }

        return true;
    }

    private static boolean checkReplaceCase(String s1, String s2) {
        boolean foundDifference = false;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                if (foundDifference) {
                    return false;
                }
                foundDifference = true;
            }
        }
        return true;
    }

}
