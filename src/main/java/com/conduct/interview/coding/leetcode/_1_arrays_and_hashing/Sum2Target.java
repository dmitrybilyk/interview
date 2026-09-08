package com.conduct.interview.coding.leetcode._1_arrays_and_hashing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// LC 1 — Two Sum
// Ще один варіант класичної задачі Two Sum через HashMap.
// Час O(n), Пам'ять O(n)
public class Sum2Target {
    public static void main(String[] args) {
        Integer[] array = {3, 5, 7, 2};
        Arrays.stream(findTwoNumbers(array, 7)).forEach(System.out::println);
    }

    public static int[] findTwoNumbers(Integer[] array, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        // значення елемента -> його індекс

        for (int i = 0; i < array.length; i++) {
            int complement = target - array[i];
            // скільки бракує до target разом з поточним елементом

            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
                // доповнення вже зустрічалось раніше -> повертаємо пару індексів
            } else {
                map.put(array[i], i);
                // запам'ятовуємо поточний елемент для майбутніх перевірок
            }
        }
        return new int[]{};
        // пари не знайдено
    }
}
