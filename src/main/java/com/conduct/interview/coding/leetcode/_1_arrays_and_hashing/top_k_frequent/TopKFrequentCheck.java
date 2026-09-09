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

        for (int i = 0; i < integers.length; i++) {
            if (map.containsKey(i)) {
                List<Integer> list = bucket[i];
                if (list == null) {
                    List<Integer> newList = new ArrayList<>();
                    newList.add(i);
                    bucket[map.get(i)] = newList;
                } else {
                    list.add(i);
                }
            }
        }

        int[] result = new int[integers.length];
        return result;
    }
}
