package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.two_sum;

import java.util.HashMap;
import java.util.Map;

// LC 1 — Two Sum
// Задача: дано масив чисел `ar` і число `num`. Знайти ДВА елементи масиву,
// сума яких дорівнює num, і повернути їхні індекси.
//
// Швидкий варіант через HashMap — за ОДИН прохід масиву, без вкладеного циклу.
// Ідея: для кожного елемента рахуємо "доповнення" (complement) — те число,
// якого не вистачає до num разом з поточним елементом. Якщо це доповнення
// вже зустрічалось раніше (є в мапі) — пару знайдено.
// Час O(n), Пам'ять O(n)
public class TwoSumHashMapIndices {

    public static String findComplementMap(int[] ar, int num) {
        Map<Integer, Integer> map = new HashMap<>();
        // map: значення елемента -> його індекс (те, що ми вже бачили)

        for (int i = 0; i < ar.length; i++) {
            int complement = num - ar[i];
            // complement — те число, якого не вистачає до num разом з ar[i]

            if (map.containsKey(complement)) {
                return map.get(complement) + "," + i;
                // доповнення вже зустрічалось раніше — знайшли пару, повертаємо ІНДЕКСИ обох
            } else {
                map.put(ar[i], i);
                // ще не зустрічали — запам'ятовуємо поточний елемент і йдемо далі
            }
        }
        return "";
        // пару так і не знайшли
    }

    public static void main(String[] args) {
        System.out.println(findComplementMap(new int[]{3, 5, 2, 1}, 5)); // "0,2" (3+2=5)
    }
}
