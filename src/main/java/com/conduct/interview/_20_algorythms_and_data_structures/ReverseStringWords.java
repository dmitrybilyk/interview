package com.conduct.interview._20_algorythms_and_data_structures;

public class ReverseStringWords {
    public static void main(String[] args) {
        String s = "Hello World Dmytro";
        System.out.println(reverseString(s));
    }

    private static String reverseString(String input) {
        String[] array = input.split("\\s+");
        int left = 0, right = array.length - 1;

        while (left < right) {
            String temp = array[right];
            array[right] = array[left];
            array[left] = temp;
            left++;
            right--;
        }


        return String.join(" ", array);
    }
}
