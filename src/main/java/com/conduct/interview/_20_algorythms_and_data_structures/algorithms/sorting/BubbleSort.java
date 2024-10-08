package com.conduct.interview._20_algorythms_and_data_structures.algorithms.sorting; // Java program

// for
// implementation of Bubble Sort

class BubbleSort {
  void bubbleSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++)
      for (int j = 0; j < n - i - 1; j++)
        if (arr[j] > arr[j + 1]) {
          // swap arr[j+1] and arr[j]
          int temp = arr[j];
          arr[j] = arr[j + 1];
          arr[j + 1] = temp;
        }
  }

  /* Prints the array */
  void printArray(int[] arr) {
    for (int j : arr) System.out.print(j + " ");
    System.out.println();
  }

  // Driver method to test above
  public static void main(String[] args) {
    BubbleSort ob = new BubbleSort();
    int[] arr = {5, 1, 4, 2, 8};
    ob.bubbleSort(arr);
    System.out.println("Sorted array");
    ob.printArray(arr);
  }
}
/* This code is contributed by Rajat Mishra */
