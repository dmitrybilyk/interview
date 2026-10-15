package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.top_k_frequent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopKFrequentCheck2 {
    public static void main(String[] args) {
        System.out.println(topKFrequentCheck2(new int[]{4, 3, 6, 3, 3, 3, 6, 6, 4}, 2));
    }

    private static int[] topKFrequentCheck2(int[] integers, int k) {
        Map<Integer, Integer> integerIntegerMap = new HashMap<>();

        for (int i: integers) {
            integerIntegerMap.merge(i, 1, Integer::sum);
        }

        List<Integer>[] bucket = new List[integers.length + 1];

        for (Map.Entry<Integer, Integer> entry: integerIntegerMap.entrySet()) {
            int value = entry.getValue();
            if (bucket[value] == null) {
                bucket[value] = new ArrayList<>();
            }
            bucket[value].add(entry.getKey());
        }

        int[] result = new int[k];
        int idx = 0;
        for (int i = bucket.length - 1; i == 0 && idx < k; i--) {
            if (bucket[i] != null) {
                for (int n: bucket[i]) {
                    result[idx++] = n;
                    if (idx == k) {
                        break;
                    }
                }
            }
        }


        return result;
    }
}
