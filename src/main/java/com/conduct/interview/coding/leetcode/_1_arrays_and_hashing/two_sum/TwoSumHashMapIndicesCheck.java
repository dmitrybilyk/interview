package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.two_sum;

import java.util.HashMap;
import java.util.Map;

public class TwoSumHashMapIndicesCheck {
    public static void main(String[] args) {
        System.out.println(twoSumHashMapIndicesCheck(new int[]{4, 2, 6, 1, 9, 5}, 15));
    }

    private static String twoSumHashMapIndicesCheck(int[] ints, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < ints.length; i++) {
            int complement = target - ints[i];
            if (map.containsKey(complement)) {
                return map.get(complement) + " " + i;
            } else {
                map.put(ints[i], i);
            }
        }
        return "";
    }
}
