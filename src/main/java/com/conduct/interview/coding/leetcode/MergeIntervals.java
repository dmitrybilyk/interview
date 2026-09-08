package com.conduct.interview.coding.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// LC 56 — Merge Intervals
// Pattern: Sort by start, then merge overlapping intervals greedily
// Time O(n log n), Space O(n)
public class MergeIntervals {

    public static int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        List<int[]> result = new ArrayList<>();
        int[] current = intervals[0];
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= current[1]) {
                current[1] = Math.max(current[1], intervals[i][1]); // extend
            } else {
                result.add(current);
                current = intervals[i];
            }
        }
        result.add(current);
        return result.toArray(new int[0][]);
    }

    public static void main(String[] args) {
        int[][] res = merge(new int[][]{{1,3},{2,6},{8,10},{15,18}});
        for (int[] r : res) System.out.println(Arrays.toString(r)); // [1,6] [8,10] [15,18]
    }
}
