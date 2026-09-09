package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.contains_duplicate;

import java.util.HashSet;
import java.util.Set;

// LC 217 — Contains Duplicate
// Pattern: HashSet — add each element; if already present → duplicate
// Time O(n), Space O(n)

// Задача: чи є в масиві хоч одне число, що повторюється.
// Ідея: складаємо елементи в HashSet один за одним.
// Set.add() повертає false, якщо елемент УЖЕ там був — це і є ознака дублікату.
public class ContainsDuplicate {

    public static boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        // множина унікальних чисел, які вже зустрічались

        for (int n : nums) {
            if (!seen.add(n)) {
                return true;
                // add() повернув false -> число вже було в множині -> знайдено дублікат
            }
        }

        return false;
        // пройшли весь масив і жодного повтору не знайшли
    }

    public static void main(String[] args) {
        System.out.println(containsDuplicate(new int[]{1, 2, 3, 1})); // true
        System.out.println(containsDuplicate(new int[]{1, 2, 3, 4})); // false
    }
}
