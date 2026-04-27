package com.conduct.interview.coding.arrays.are_arrays_rotations;

public class AreArraysRotationsCheck2 {
    public static void main(String[] args) {
        int[] array1 = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] array2 = {5, 6, 7, 8, 1, 2, 3, 4};
        System.out.println(areArraysRotations2(array1, array2));
    }

    private static boolean areArraysRotations2(int[] array1, int[] array2) {
        if (array1.length == 0 || array1.length != array2.length) {
            return false;
        }
        int shift = -1;

        for (int i = 0; i < array2.length; i++) {
            if (array1[0] == array2[i]) {
                shift = i;
                break;
            }
        }

        if (shift == -1) {
            return false;
        }

        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[(shift + i) % array1.length]) {
                return false;
            }
        }

        return true;
    }
}
