package com.conduct.interview._20_algorythms_and_data_structures.algorithms.search.binary_search;

public class IterativeBinarySearch {
  public static void main(String[] args) {
    int[] array = {2, 4, 6, 7, 8, 9};
    binarySearch(array, 8);
  }

  private static void binarySearch(int[] array, int key) {
    int firstIndex = 0;
    int lastIndex = array.length - 1;
    int midIndex = (lastIndex - firstIndex) / 2;

    while (firstIndex <= lastIndex) {
      if (array[midIndex] == key) {
        System.out.println("Element is found at index " + midIndex);
        return;
      } else if (array[midIndex] >= key) {
        lastIndex = midIndex - 1;
      } else {
        firstIndex = midIndex + 1;
      }
      midIndex = firstIndex + (lastIndex - firstIndex) / 2;
    }
    System.out.println("Element Not Found");
  }
}
