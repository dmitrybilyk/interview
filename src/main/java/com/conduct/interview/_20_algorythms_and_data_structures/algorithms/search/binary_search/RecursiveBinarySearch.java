package com.conduct.interview._20_algorythms_and_data_structures.algorithms.search.binary_search;

public class RecursiveBinarySearch {
  public static void main(String[] args) {
    int[] sortedArray = {2, 4, 5, 7, 8};
    int result = binarySearch(sortedArray, 0, sortedArray.length - 1, 6);
    if (result == -1) {
      System.out.println("No element found");
    } else {
      System.out.println("Found " + result);
    }
  }

  private static int binarySearch(int[] arr, int first, int last, int key) {
    if (last >= first) {
      int mid = last - first / 2;

      // If the element is present at the
      // middle itself
      if (arr[mid] == key) return mid;

      // If element is smaller than mid, then
      // it can only be present in left subarray
      if (arr[mid] > key) return binarySearch(arr, first, mid - 1, key);

      // Else the element can only be present
      // in right subarray
      return binarySearch(arr, mid + 1, last, key);
    }

    // We reach here when element is not present
    // in array
    return -1;
  }
}
