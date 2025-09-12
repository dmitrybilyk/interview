package com.conduct.interview._20_algorythms_and_data_structures.algorithms.search.binary_search;

public class BinarySearchCheck {
    public static void main(String[] args) {
        int[] array = new int[]{1, 4, 6, 7, 9, 12, 23, 45, 67};
//        System.out.println(iterativeBinarySearch(array, 1));
        System.out.println(recursiveBinarySearch(array, 1));
    }

    private static int iterativeBinarySearch(int[] array, int target) {
        int leftIndex = 0;
        int rightIndex = array.length - 1;
        int midIndex = leftIndex + (rightIndex - leftIndex)/2;

        while(leftIndex <= rightIndex) {
            if (target == array[midIndex]) {
                return midIndex;
            } else if (array[midIndex] > target) {
                rightIndex = midIndex - 1;
            } else {
                leftIndex = midIndex + 1;
            }
            midIndex = leftIndex + (rightIndex - leftIndex)/2;
        }

        return -1;
    }

    private static int recursiveBinarySearch(int[] array, int target) {
        return binarySearch(array, array.length - 1, (array.length - 1)/2, target);
    }

    private static int binarySearch(int[] array, int leftIndex, int rightIndex, int target) {
        int midIndex = leftIndex + (rightIndex - leftIndex)/2;

        if (target == array[midIndex]) {
            return midIndex;
        } else if (array[midIndex] > target) {
            binarySearch(array, leftIndex, rightIndex = midIndex - 1, target);
        } else {
            binarySearch(array, midIndex + 1, rightIndex, target);
        }
        return -1;
    }
}
