package com.conduct.interview.coding.strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindNoneRepeatingChars {
    public static void main(String[] args) {
        String value = "dfdfdfsa";
        System.out.println(findNoneRepeatingChars(value));
    }

    private static List<Character> findNoneRepeatingChars(String value) {
        Map<Character, Integer> characterIntegerMap = new HashMap<>();
        List<Character> characters = new ArrayList<>();
        char[] charArray = value.toCharArray();
        for (char c : charArray) {
            characterIntegerMap.compute(c, (key, val) -> val == null ? 1 : val + 1);
        }

        for (char c : charArray) {
            if (characterIntegerMap.get(c) == 1) {
                characters.add(c);
            }
        }
        return characters;
    }
}
