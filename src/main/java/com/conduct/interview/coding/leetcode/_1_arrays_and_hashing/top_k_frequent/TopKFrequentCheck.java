package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.top_k_frequent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopKFrequentCheck {
    public static void main(String[] args) {
        System.out.println(topKFrequentCheck(new int[]{4, 3, 6, 3, 3, 3, 6, 6, 4}, 2));
    }

    private static int[] topKFrequentCheck(int[] integers, int k) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int integer : integers) {
            map.merge(integer, 1, Integer::sum);
        }

        List<Integer>[] bucket = new List[integers.length];

        for (Map.Entry<Integer, Integer> integerIntegerEntry : map.entrySet()) {
            if (bucket[integerIntegerEntry.getValue()] == null) {
                bucket[integerIntegerEntry.getValue()] = new ArrayList<>();
            }
            bucket[integerIntegerEntry.getValue()].add(integerIntegerEntry.getKey());
        }

        int idx = 0;
        int[] result = new int[k];
        for (int i = bucket.length - 1; i >= 0 && idx < k; i--) {
            if (bucket[i] != null) {
                for (Integer integerResult : bucket[i]) {
                    result[idx++] = integerResult;
                    if (idx == k) {
                        break;
                    }

                }
            }
        }

        return new int[]{};
    }
}
