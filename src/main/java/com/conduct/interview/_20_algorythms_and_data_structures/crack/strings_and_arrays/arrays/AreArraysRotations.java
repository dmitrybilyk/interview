package com.conduct.interview._20_algorythms_and_data_structures.crack.strings_and_arrays.arrays;

public class AreArraysRotations {
    public static void main(String[] args) {
        int[] array1 = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] array2 = {5, 6, 7, 8, 1, 2, 3, 4};

        System.out.println(checkIfArraysRotations(array1, array2));
    }

    private static boolean checkIfArraysRotations(int[] array1, int[] array2) {
        if (array1.length != array2.length) return false;
        if (array1.length == 0) return false;

        int shift = -1;

        // Find where array1[0] appears in array2
        for (int i = 0; i < array2.length; i++) {
            if (array2[i] == array1[0]) {
                shift = i;
                break;
            }
        }

        if (shift == -1) return false;

        // Check if rotation matches
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[(shift + i) % array1.length]) {
                return false;
            }
        }

        return true;
    }
}
