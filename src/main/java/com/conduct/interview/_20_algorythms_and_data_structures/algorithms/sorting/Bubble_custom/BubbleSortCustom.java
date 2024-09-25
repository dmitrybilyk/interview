package com.conduct.interview._20_algorythms_and_data_structures.algorithms.sorting.Bubble_custom;

import java.util.Arrays;

public class BubbleSortCustom {
  public static void main(String[] args) {
    var array = new Integer[] {3, 4, 13, 3, 1};
    sort(array);
    System.out.println(Arrays.toString(array));
    sort(array);
  }

  private static void sort(Integer[] array) {
    for (int i = 0; i < array.length; i++) {
      for (int j = 0; j < array.length - i - 1; j++) {
        if (array[j] < array[j + 1]) {
          var temp = array[j];
          array[j] = array[j + 1];
          array[j + 1] = temp;
        }
      }
    }
  }
}
