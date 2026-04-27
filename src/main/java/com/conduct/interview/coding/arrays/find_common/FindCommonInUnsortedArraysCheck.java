package com.conduct.interview.coding.arrays.find_common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindCommonInUnsortedArraysCheck {
    public static void main(String[] args) {
        int[] array1 = {1, 2, 4, 6, 8, 10};
        int[] array2 = {1, 5, 7, 9, 10, 3};
        System.out.println(findCommonInUnSorted(array1, array2));
    }

    private static List<Integer> findCommonInUnSorted(int[] array1, int[] array2) {
        Set<Integer> common = new HashSet<>();
        List<Integer> result = new ArrayList<>();

        for (int i : array1) {
            common.add(i);
        }

        for (int i : array2) {
            if (common.contains(i)) {
                result.add(i);
                common.remove(i);
            }
        }

        return result;
    }
}
