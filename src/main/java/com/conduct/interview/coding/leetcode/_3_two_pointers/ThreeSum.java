package com.conduct.interview.coding.leetcode._3_two_pointers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// LC 15 — 3Sum
// Pattern: Sort + two pointers. Fix i, then use left/right to find pairs summing to -nums[i]
// Skip duplicates at each level. Time O(n²), Space O(1)

// Задача: знайти всі унікальні трійки чисел, сума яких = 0.
// Ідея: спочатку сортуємо масив. Потім фіксуємо перше число nums[i]
// і шукаємо пару (lo, hi) серед решти елементів через two-pointer,
// так само як у звичайному Two Sum на відсортованому масиві.
public class ThreeSum {

    public static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        // сортування обов'язкове — без нього two-pointer не працює

        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue; // skip duplicate pivots
                // якщо це те саме число, що й попереднє i — трійки з ним уже перебрали, пропускаємо
            }

            int lo = i + 1, hi = nums.length - 1;
            // шукаємо пару (lo, hi) серед елементів, що йдуть після i

            while (lo < hi) {
                int sum = nums[i] + nums[lo] + nums[hi];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[lo], nums[hi]));
                    // знайшли трійку з сумою 0 — зберігаємо

                    while (lo < hi && nums[lo] == nums[lo + 1]) {
                        lo++;
                    }
                    while (lo < hi && nums[hi] == nums[hi - 1]) {
                        hi--;
                    }
                    // пропускаємо дублікати зліва і справа, щоб не додати ту саму трійку двічі

                    lo++;
                    hi--;
                    // звужуємо вікно й шукаємо далі

                } else if (sum < 0) {
                    lo++;
                    // сума замала (масив відсортований) -> збільшуємо, рухаючи lo вправо
                } else {
                    hi--;
                    // сума завелика -> зменшуємо, рухаючи hi вліво
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(threeSum(new int[]{-1, 0, 1, 2, -1, -4})); // [[-1,-1,2],[-1,0,1]]
    }
}
