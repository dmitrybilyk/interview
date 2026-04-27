package com.conduct.interview.coding.arrays.find_common;

import java.util.ArrayList;
import java.util.List;

public class CommonElementsInSortedArraysCheck {
    public static void main(String[] args) {
        int[] array1 = {1, 2, 4, 6, 8, 10};
        int[] array2 = {1, 5, 7, 9, 10};
        System.out.println(findCommonInSorted(array1, array2));
    }

    private static List<Integer> findCommonInSorted(int[] array1, int[] array2) {
        List<Integer> commonElements = new ArrayList<>();

        int p1 = 0;
        int p2 = 0;
        while (p1 < array1.length && p2 < array2.length) {
            if (array1[p1] == array2[p2]) {
                commonElements.add(array1[p1]);
                p1++;
                p2++;
            } else if (array1[p1] < array2[p2]) {
                p1++;
            } else {
                p2++;
            }
        }

        return commonElements;
    }

}
