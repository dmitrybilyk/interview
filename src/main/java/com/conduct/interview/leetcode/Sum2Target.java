package com.conduct.interview.leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Sum2Target {
    public static void main(String[] args) {
        Integer[] array = {3, 5, 7, 2};
        Arrays.stream(findTwoNumbers(array, 7)).forEach(System.out::println);
    }

    public static int[] findTwoNumbers(Integer[] array, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            int complement = target - array[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            } else {
                map.put(array[i], i);
            }
        }
        return new int[]{};
    }
}
