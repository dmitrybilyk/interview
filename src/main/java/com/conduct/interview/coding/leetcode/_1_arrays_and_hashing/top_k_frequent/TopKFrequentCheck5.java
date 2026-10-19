package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.top_k_frequent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopKFrequentCheck5 {
    public static void main(String[] args) {
        System.out.println(topKFrequentCheck(new int[]{4, 3, 6, 3, 3, 3, 6, 6, 4}, 2));
    }

    private static int[] topKFrequentCheck(int[] integers, int k) {
        Map<Integer, Integer> merged = new HashMap<>();

        for (int n: integers) {
            merged.merge(n, 1, Integer::sum);
        }

        List<Integer>[] bucket = new List[integers.length + 1];

        for (Map.Entry<Integer, Integer> integerIntegerEntry : merged.entrySet()) {
            if (bucket[integerIntegerEntry.getValue()] == null) {
                bucket[integerIntegerEntry.getValue()] = new ArrayList<>();
            }
            bucket[integerIntegerEntry.getValue()].add(integerIntegerEntry.getKey());
        }

        int idx = 0;
        int[] result = new int[k];

        for (int i = bucket.length - 1; i >= 0 && idx < k; i--) {
            if (bucket[i] != null) {
                for (Integer integer : bucket[i]) {
                    result[idx++] = integer;
                    if (idx == k) {
                        break;
                    }
                }
            }
        }
        return result;
    }
}
