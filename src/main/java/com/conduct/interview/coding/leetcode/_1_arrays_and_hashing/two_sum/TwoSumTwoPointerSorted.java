package com.conduct.interview.coding.leetcode._1_arrays_and_hashing.two_sum;

// LC 1 — Two Sum (варіант для ВІДСОРТОВАНОГО масиву)
// Задача: дано ВІДСОРТОВАНИЙ масив чисел `nums` і число `target`. Знайти ДВА елементи,
// сума яких дорівнює target.
//
// Two-pointer підхід — працює ТІЛЬКИ якщо масив вже відсортований.
// Два вказівники з обох кінців масиву сходяться до середини:
// якщо поточна сума замала — рухаємо лівий вказівник вправо (щоб збільшити суму),
// якщо завелика — рухаємо правий вказівник вліво (щоб зменшити суму).
// Час O(n), Пам'ять O(1) — не потребує додаткової мапи, на відміну від HashMap-варіантів,
// але вимагає, щоб вхідні дані вже були відсортовані.
public class TwoSumTwoPointerSorted {

    public static String onSorted(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        // два вказівники з обох кінців масиву

        for (int i = 0; i < nums.length; i++) {
            int result = nums[left] + nums[right];
            if (result == target) {
                return nums[left] + ", " + nums[right];
                // сума збіглась — знайшли пару (повертаємо ЗНАЧЕННЯ)

            } else if (result < target) {
                left++;
                // сума замала — рухаємо лівий вказівник вправо, щоб збільшити суму

            } else {
                right--;
                // сума завелика — рухаємо правий вказівник вліво, щоб зменшити суму
            }
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(onSorted(new int[]{1, 2, 3, 5}, 5)); // "2, 3"
    }
}
