package com.conduct.interview.coding.strings.first_repeating_char;

public class FirstRepeatingCharCheck {
    public static void main(String[] args) {
        String s = "dfghjdslf";
        System.out.println(firstRepeatingChar(s));
    }

    private static char firstRepeatingChar(String s) {
        int[] occurrences = new int[256];

        for (char c : s.toCharArray()) {
            if (occurrences[c] > 0) {
                return c;
            }
            occurrences[c] = occurrences[c] + 1;
        }
        return '0';
    }
}
