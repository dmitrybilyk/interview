package com.conduct.interview.coding.arrays.find_common;

import java.util.ArrayList;
import java.util.List;

public class CommonElementsInSortedArrays {
    public static void main(String[] args) {
        int[] array1 = {2, 4, 5, 7, 8};
        int[] array2 = {1, 3, 5, 8, 18};

        System.out.println(findCommonElements(array1, array2));
    }

    private static List<Integer> findCommonElements(int[] array1, int[] array2) {
        List<Integer> commonElements = new ArrayList<>();
        int p1 = 0;
        int p2 = 0;

        while(p1 < array1.length && p2 < array2.length) {
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
