package com.conduct.interview._20_algorythms_and_data_structures.algorithms.sorting;

public class MergeSort {

	public static void main(String[] args) {
		int[] arrayToSort = { 5, 1, 6, 2, 3, 4 };
       	MergeSort.mergeSort(arrayToSort, arrayToSort.length);
		printArray(arrayToSort);
	}

	public static void mergeSort(int[] array, int length) {
	    if (length < 2) {
	        return;
	    }
	    int middle = length / 2;
	    int[] leftHalf = new int[middle];
	    int[] rightHalf = new int[length - middle];

	    for (int i = 0; i < middle; i++) {
	        leftHalf[i] = array[i];
	    }
	    for (int i = middle; i < length; i++) {
	        rightHalf[i - middle] = array[i];
	    }
	    mergeSort(leftHalf, middle);
	    mergeSort(rightHalf, length - middle);

	    merge(array, leftHalf, rightHalf, middle, length - middle);
	}

	public static void merge(
	  	int[] array, int[] leftHalf, int[] rightHalf, int leftSize, int rightSize) {
	 
	    int i = 0, j = 0, k = 0;
	    while (i < leftSize && j < rightSize) {
	        if (leftHalf[i] <= rightHalf[j]) {
	            array[k++] = leftHalf[i++];
	        }
	        else {
	            array[k++] = rightHalf[j++];
	        }
	    }
	    while (i < leftSize) {
	        array[k++] = leftHalf[i++];
	    }
	    while (j < rightSize) {
	        array[k++] = rightHalf[j++];
	    }
	}

	private static void printArray(int[] array) {
		for (int j : array) {
			System.out.println(j);
		}
	}
}