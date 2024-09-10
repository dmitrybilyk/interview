package com.conduct.interview._20_algorythms_and_data_structures.algorithms.sorting;

public class QuickSort {
    public static void main(String[] args) {
        int[] arrayToSort = {5, 1, 6, 2, 3, 4};
        quickSort(arrayToSort, 0, arrayToSort.length - 1);
        for (int i = 0; i < arrayToSort.length; i++) {
            System.out.println(arrayToSort[i]);
        }
    }

    private static void quickSort(int[] arr, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex - 1);
            quickSort(arr, partitionIndex + 1, end);
        }
    }

    private static int partition(int[] arr, int begin, int end) {
        int pivot = arr[end];
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            if (arr[j] <= pivot) {
                i++;

                int swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        int swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;

        return i + 1;
    }
}
