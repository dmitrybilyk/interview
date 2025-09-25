package com.conduct.interview.coding.strings.non_repeating_chars;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NonRepeatingCharsCheck {
    public static void main(String[] args) {
        String s = "adsfgghj";
        System.out.println(nonRepeatingChars(s));
    }

    private static String nonRepeatingChars(String s) {
        Map<Character, Integer> characterMap = new HashMap<>();
        for (char c : s.toCharArray()) {
            characterMap.compute(c, (character, integer) -> integer == null ? 1 : integer + 1);
        }
        return characterMap.entrySet().stream().filter(characterIntegerEntry ->
                characterIntegerEntry.getValue() == 1).map(Map.Entry::getKey).toList().toString();
    }
}
