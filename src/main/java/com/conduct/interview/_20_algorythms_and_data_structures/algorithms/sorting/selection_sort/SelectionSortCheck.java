package com.conduct.interview._20_algorythms_and_data_structures.algorithms.sorting.selection_sort;

import java.util.Arrays;

public class SelectionSortCheck {
    public static void main(String[] args) {
        int[] array = {3, 5, 7, 1, 3};
        System.out.println(Arrays.toString(selectionSort(array)));
    }

    private static int[] selectionSort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[minIndex] ) {
                    minIndex = j;
                }
            }
            int temp = array[i];
            array[i] = array[minIndex];
            array[minIndex] = temp;
        }
        return array;
    }
}
