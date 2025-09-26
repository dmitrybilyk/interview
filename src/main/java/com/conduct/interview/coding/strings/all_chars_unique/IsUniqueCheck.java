package com.conduct.interview.coding.strings.all_chars_unique;

import java.util.HashSet;
import java.util.Set;

public class IsUniqueCheck {
    public static void main(String[] args) {
        String s = "qwertyui";
//        System.out.println(isUnique(s));
        System.out.println(isUniqueWithSet(s));
    }

    private static boolean isUniqueWithSet(String s) {
        Set<Character> characterSet = new HashSet<>();
        for (int i = 0; i < s.length(); i++) {
            if (!characterSet.add(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isUnique(String s) {
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j < s.length(); j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }
}
