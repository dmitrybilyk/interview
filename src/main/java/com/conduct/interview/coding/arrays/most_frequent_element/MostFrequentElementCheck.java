package com.conduct.interview.coding.arrays.most_frequent_element;

import java.util.HashMap;
import java.util.Map;

public class MostFrequentElementCheck {
    public static void main(String[] args) {
        int[] array = {4, 56, 3, 5, 3, 6, 3, 6};
        System.out.println(mostFrequentElement(array));
    }

    private static int mostFrequentElement(int[] array) {
        Map<Integer, Integer> map = new HashMap<>();

        int maxCount = 0;
        int mostFrequent = -1;
        for (int i = 0; i < array.length - 1; i++) {
            int count = map.compute(array[i], (key, value) -> value == null ? 1 : value + 1);
            if (count > maxCount) {
                maxCount = count;
                mostFrequent = array[i];
            }
        }

        return mostFrequent;
    }
}
