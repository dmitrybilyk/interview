package com.conduct.interview._20_algorythms_and_data_structures.algorithms.search.binary_search;

public class IterativeBinarySearch {
  public static void main(String[] args) {
    int[] array = {2, 4, 6, 7, 8, 9};
    binarySearch2(array, 8);
//    binarySearchMySelf(array, 8);
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


  private static void binarySearchMySelf(int[] array, int key) {
    int startIndex = 0;
    int endIndex = array.length - 1;
//    int midIndex = (startIndex + endIndex) / 2;
    int midIndex = (endIndex - startIndex) / 2;

    while(startIndex <= endIndex) {
      if (array[midIndex] == key) {
        System.out.println("Found at index " + midIndex);
        return;
      } else if (array[midIndex] >= key) {
        endIndex = midIndex - 1;
      } else {
        startIndex = midIndex + 1;
      }
      midIndex = startIndex + (endIndex - startIndex) / 2;
    }
    System.out.println("Not Found");
  }


  public static void binarySearch2(int[] array, int key) {
    int startIndex = 0;
    int endIndex = array.length - 1;

    while (startIndex <= endIndex) {
      int midIndex = (startIndex + endIndex) / 2;
      if (array[midIndex] == key) {
        System.out.println("Found at - " + midIndex);
        return;
      } else if (array[midIndex] > key) {
        endIndex = midIndex - 1;
      } else {
        startIndex = midIndex + 1;
      }
    }
    System.out.println("Not found");
  }

}
