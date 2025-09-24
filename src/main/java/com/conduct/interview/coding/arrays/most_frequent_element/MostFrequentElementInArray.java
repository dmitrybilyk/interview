package com.conduct.interview.coding.arrays.most_frequent_element;

import java.util.HashMap;
import java.util.Map;

public class MostFrequentElementInArray {
    public static void main(String[] args) {
        int[] array = {4, 2, 3, 6, 7, 2};

        System.out.println(mostFrequentElement(array));
    }

    private static int mostFrequentElement(int[] array) {
        Map<Integer, Integer> map = new HashMap<>();
        Integer mostFrequent = null;
        int maxCount = 0;
        for (int i : array) {
            int count = map.compute(i, (mapKey, mapValue) -> mapValue == null ? 1 : mapValue + 1);
            if (count > maxCount) {
                maxCount = count;
                mostFrequent = i;
            }
        }
        return mostFrequent;
    }
}
