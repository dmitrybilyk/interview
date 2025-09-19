package com.conduct.interview.coding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RemoveDuplicates {
    public static void main(String[] args) {
        int[] array = new int[]{4, 34, 56, 7, 3, 1, 3, 4};
        System.out.println(Arrays.toString(removeDuplicates(array)));
    }

    private static int[] removeDuplicates(int[] array) {
        Map<Integer, Integer> map = new HashMap<>();
        int[] resultArray = new int[array.length];
        for(int i = 0; i < array.length -1; i++) {
            int key = array[i];
            map.get(key);
            if (map.get(key) == null) {
                resultArray[i] = key;
                map.put(array[i], i);
            }
        }
        return resultArray;
    }
}
