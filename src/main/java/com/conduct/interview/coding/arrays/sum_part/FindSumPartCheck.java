package com.conduct.interview.coding.arrays.sum_part;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FindSumPartCheck {
    public static void main(String[] args) {
        int[] array = {4, 6, 2, 7, 3, 1};
        System.out.println(Arrays.toString(findSumPart(array, 13)));
    }

    public static int[] findSumPart(int[] array, int total) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < array.length - 1; i++) {
            int diff = total - array[i];
            Integer firstPosition = map.get(diff);
            if (firstPosition != null) {
                return new int[]{firstPosition, i};
            } else {
                map.put(array[i], i);
            }
        }

        return new int[2];
    }
}
