package com.conduct.interview.coding.leetcode._1_arrays_and_hashing;

import java.util.Arrays;

// LC 238 — Product of Array Except Self
// Pattern: Prefix product left pass, then suffix product right pass — no division
// Time O(n), Space O(1) output array excluded

// Задача: для кожного індексу i порахувати добуток УСІХ елементів, КРІМ nums[i].
// Ділити не можна (могли б бути нулі). Тому робимо два проходи:
// 1) зліва направо — накопичуємо добуток усіх елементів ДО i (prefix)
// 2) справа наліво — домножуємо на добуток усіх елементів ПІСЛЯ i (suffix)
public class ProductExceptSelf {

    public static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        result[0] = 1;
        // для першого елемента немає нічого зліва -> добуток зліва = 1

        for (int i = 1; i < n; i++) result[i] = result[i - 1] * nums[i - 1]; // prefix
        // result[i] = добуток усіх елементів nums[0..i-1] (усе, що зліва від i)

        int suffix = 1;
        // накопичувач добутку елементів, що йдуть ПІСЛЯ поточного i

        for (int i = n - 1; i >= 0; i--) {
            result[i] *= suffix;
            // домножуємо вже наявний "лівий" добуток на "правий" -> отримуємо повний результат для i

            suffix *= nums[i]; // accumulate suffix on the fly
            // додаємо поточний елемент у накопичувач для наступних (лівіших) ітерацій
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(productExceptSelf(new int[]{1, 2, 3, 4}))); // [24,12,8,6]
    }
}
