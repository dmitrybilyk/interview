package com.conduct.interview.coding.strings.reverse_string_words;

public class ReverseStringWordsCheck {
    public static void main(String[] args) {
        String s = "This is my string";
        System.out.println(reverseString(s));
    }

    private static String reverseString(String s) {
        String[] array = s.split(" ");
        int left = 0, right = array.length -1;

        while(left < right) {
            String temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
        return String.join(" ", array);
    }
}
