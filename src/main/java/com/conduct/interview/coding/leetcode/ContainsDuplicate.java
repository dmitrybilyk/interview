package com.conduct.interview.coding.leetcode;

import java.util.HashSet;
import java.util.Set;

// LC 217 — Contains Duplicate
// Pattern: HashSet — add each element; if already present → duplicate
// Time O(n), Space O(n)
public class ContainsDuplicate {

    public static boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int n : nums) if (!seen.add(n)) return true;
        return false;
    }

    public static void main(String[] args) {
        System.out.println(containsDuplicate(new int[]{1, 2, 3, 1})); // true
        System.out.println(containsDuplicate(new int[]{1, 2, 3, 4})); // false
    }
}
