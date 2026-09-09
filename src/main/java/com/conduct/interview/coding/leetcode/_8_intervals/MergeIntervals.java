package com.conduct.interview.coding.leetcode._8_intervals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// LC 56 — Merge Intervals
// Pattern: Sort by start, then merge overlapping intervals greedily
// Time O(n log n), Space O(n)

// Задача: об'єднати всі інтервали, що перетинаються, в мінімальний набір непересічних інтервалів.
// Ідея: спочатку сортуємо за початком інтервалу — тоді перетини можуть бути тільки
// із "сусіднім поточним" інтервалом, і можна пройти масив жадібно за один прохід.
public class MergeIntervals {

    public static int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        // сортуємо за початком інтервалу (a[0]) — без цього жадібний прохід не спрацює

        List<int[]> result = new ArrayList<>();
        int[] current = intervals[0];
        // current — інтервал, який зараз "розширюємо"

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= current[1]) {
                current[1] = Math.max(current[1], intervals[i][1]); // extend
                // наступний інтервал перетинається (чи торкається) поточного -> зливаємо їх,
                // розширюючи кінець current до максимального з двох

            } else {
                result.add(current);
                // перетину немає -> поточний інтервал закінчено, зберігаємо його

                current = intervals[i];
                // починаємо "розширювати" вже наступний інтервал
            }
        }
        result.add(current);
        // останній накопичений інтервал теж треба додати — цикл його не встиг зберегти

        return result.toArray(new int[0][]);
    }

    public static void main(String[] args) {
        int[][] res = merge(new int[][]{{1,3},{2,6},{8,10},{15,18}});
        for (int[] r : res) {
            System.out.println(Arrays.toString(r)); // [1,6] [8,10] [15,18]
        }
    }
}
