package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.two_sum;

import java.util.HashMap;
import java.util.Map;

// LC 1 — Two Sum
// Задача: дано масив чисел `nums` і число `target`. Знайти ДВА елементи масиву,
// сума яких дорівнює target.
//
// Той самий підхід через HashMap за один прохід (O(n)), що й TwoSumHashMapIndices,
// але тут повертаються самі ЗНАЧЕННЯ чисел, а не їхні індекси.
// Час O(n), Пам'ять O(n)
public class TwoSumHashMapValues {

    public static String twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        // значення елемента -> його індекс

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            // скільки бракує до target разом з поточним елементом

            if (map.containsKey(complement)) {
                return nums[i] + ", " + map.get(complement);
                // доповнення вже було в мапі -> пара знайдена, повертаємо ЗНАЧЕННЯ обох чисел
            } else {
                map.put(nums[i], i);
                // запам'ятовуємо значення -> індекс для майбутніх перевірок
            }
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(twoSum(new int[]{3, 5, 2, 1}, 5)); // "2, 0" (nums[2]=2, map.get(complement=3)=0)
    }
}
