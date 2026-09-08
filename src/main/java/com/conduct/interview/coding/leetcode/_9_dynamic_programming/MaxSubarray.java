package com.conduct.interview.coding.leetcode._9_dynamic_programming;

// LC 53 — Maximum Subarray (Kadane's Algorithm)
// Pattern: Track running sum; reset to 0 when it goes negative
// Time O(n), Space O(1)

// Задача: знайти максимальну суму серед усіх неперервних підмасивів.
// Ідея Кадане: якщо сума "хвоста", накопиченого досі, стає від'ємною —
// вона тільки шкодить майбутнім елементам, тож вигідніше почати новий підмасив з нуля.
public class MaxSubarray {

    public static int maxSubArray(int[] nums) {
        int max = nums[0], current = nums[0];
        // current — найкраща сума підмасиву, що закінчується САМЕ на поточному елементі
        // max — найкраща сума серед УСІХ підмасивів, знайдених до цього моменту

        for (int i = 1; i < nums.length; i++) {
            current = Math.max(nums[i], current + nums[i]);
            // або продовжуємо попередній підмасив (current + nums[i]),
            // або починаємо новий рівно з nums[i] — якщо попередній "хвіст" був від'ємним

            max = Math.max(max, current);
            // оновлюємо загальний максимум, якщо поточний підмасив кращий
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println(maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4})); // 6
        System.out.println(maxSubArray(new int[]{1}));                               // 1
        System.out.println(maxSubArray(new int[]{-1, -2}));                          // -1
    }
}
