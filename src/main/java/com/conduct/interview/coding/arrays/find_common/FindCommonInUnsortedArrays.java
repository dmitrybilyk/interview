package com.conduct.interview.coding.arrays.find_common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindCommonInUnsortedArrays {
    public static void main(String[] args) {
        int[] array1 = {3, 5, 2, 1, 6, 2};
        int[] array2 = {4, 2, 7, 1, 6, 3, 5, 2, 1, 6, 2};
        System.out.println(findCommon(array1, array2));
    }

    private static List<Integer> findCommon(int[] array1, int[] array2) {
        Set<Integer> common = new HashSet<>();
        List<Integer> commonElements = new ArrayList<>();

        for (int i: array1) {
            common.add(i);
        }

        for (int i: array2) {
            if (common.contains(i)) {
                commonElements.add(i);
                common.remove(i);
            }
        }

        return commonElements;
    }
}
