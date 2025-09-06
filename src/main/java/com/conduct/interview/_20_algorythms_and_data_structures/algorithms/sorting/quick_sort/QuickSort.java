package com.conduct.interview._20_algorythms_and_data_structures.algorithms.sorting.quick_sort;

import java.util.Arrays;

public class QuickSort {
    public static void main(String[] args) {
        int[] array = new int[] {3, 6, 2 , 8, 12, 7, 2, 5, 77};
        quickSort(array, 0, array.length - 1);
        System.out.println(Arrays.toString(array));
    }

    private static void quickSort(int[] array, int leftIndex, int rightIndex) {
        if (leftIndex < rightIndex) {
            int pivot = quickSortHelper(array, leftIndex, rightIndex);
            // Recursively sort left and right subarrays
            quickSort(array, leftIndex, pivot - 1);
            quickSort(array, pivot + 1, rightIndex);
        }
    }

    private static int quickSortHelper(int[] array, int leftIndex, int rightIndex) {
        int pivot = leftIndex;
        int swap = leftIndex;
        if (leftIndex < rightIndex) {
            for (int i = leftIndex + 1; i <= rightIndex; i++) {
                if (array[i] < array[pivot]) {
                    swap++;
                    int temp = array[swap];
                    array[swap] = array[i];
                    array[i] = temp;
                }
            }
            int temp = array[pivot];
            array[pivot] = array[swap];
            array[swap] = temp;
            return swap;
        }
        return pivot;
    }
}
