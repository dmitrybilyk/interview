package com.conduct.interview._20_algorythms_and_data_structures.algorithms.sorting.Bubble_custom;

import java.util.Arrays;
import java.util.List;

public class BubbleSortCheck {
    public static void main(String[] args) {
        int[] array = {3, 5, 7, 1, 3};
        System.out.println(Arrays.toString(bubbleSort(array)));
    }

    private static int[] bubbleSort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j + 1];
                    array[j + 1] = array[j];
                    array[j] = temp;
                }
            }
        }
        return array;
    }
}
