package com.conduct.interview.coding.strings.reverse_string;

import java.util.Arrays;

public class ReverseString {
    public static void main(String[] args) {
        String s = "This is my string";
        System.out.println(reverseString(s));
    }

    private static String reverseString(String s) {
        char[] charArray = s.toCharArray();
        for (int i = 0, j = charArray.length - 1; i < j; i++, j--) {
            char temp = charArray[i];
            charArray[i] = charArray[j];
            charArray[j] = temp;
        }
        return new String(charArray);
    }
}
