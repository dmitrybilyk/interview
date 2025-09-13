package com.conduct.interview.coding;

public class FirstRepeatingCharInStr {
    public static void main(String[] args) {
        String someString = "firstrepeating";
        System.out.println(firstRepeatingChar(someString));
    }

    // O(n) both time and space
    private static char firstRepeatingChar(String someString) {
        char[] charArray = someString.toCharArray();
        int[] array = new int[256];
        for (char c : charArray) {
            int value = array[c];
            if (value > 0) {
                return c;
            } else {
                array[c] = 1;
            }
        }
        return '\0';
    }
}
