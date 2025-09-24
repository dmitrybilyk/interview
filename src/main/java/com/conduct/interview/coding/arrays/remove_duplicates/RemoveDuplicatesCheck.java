package com.conduct.interview.coding.arrays.remove_duplicates;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RemoveDuplicatesCheck {
    public static void main(String[] args) {
        int[] array = {4, 5, 4, 2, 3, 5, 6};
        System.out.println(Arrays.toString(removeDuplicates(array)));
    }

    private static int[] removeDuplicates(int[] array) {
        Map<Integer, Integer> map = new HashMap<>();
        int[] withoutDuplicates = new int[array.length];

        for (int i = 0; i < array.length - 1; i++) {
            Integer value = map.get(array[i]);
            if (value == null) {
                withoutDuplicates[i] = array[i];
                map.put(array[i], i);
            }
        }
        return withoutDuplicates;
    }
}
