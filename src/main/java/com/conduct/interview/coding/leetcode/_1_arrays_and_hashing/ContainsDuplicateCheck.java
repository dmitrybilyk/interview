package com.conduct.interview.coding.leetcode._1_arrays_and_hashing;

import java.util.HashSet;
import java.util.Set;

public class ContainsDuplicateCheck {
    public static void main(String[] args) {
        System.out.println(containsDuplicateCheck(new int[]{4, 6, 2, 4}));
    }

    private static boolean containsDuplicateCheck(int[] ints) {
        Set<Integer> seen = new HashSet<>();
        for (Integer i : ints) {
            if (!seen.add(i)) {
                return true;
            }
        }
        return false;
    }
}
