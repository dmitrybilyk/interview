package com.conduct.interview.coding.strings;

import java.util.HashSet;
import java.util.Set;

public class IsUnique {

    public static void main(String[] args) {
//        String s = "This my";
        String s = "This mym";
//        System.out.println(isAllCharsUnique(s));
        System.out.println(isAllCharsUniqueWithSet(s));
    }

    private static boolean isAllCharsUnique(String s) {
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j < s.length(); j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isAllCharsUniqueWithSet(String s) {
        Set<Character> set = new HashSet<>();
        for (int i = 0; i < s.length(); i++) {
            if (!set.add(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
