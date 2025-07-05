package com.conduct.interview.leetcode;

import java.util.HashMap;
import java.util.Map;

public class Complement {
    public static String findComplementMap(int[] ar, int num) {
            Map<Integer, Integer> map = new HashMap<>();
            for (int i = 0; i < ar.length; i++) {
                int complement = num - ar[i];
                if (map.containsKey(complement)) {
                    return map.get(complement) + "," + i;
                } else {
                     map.put(ar[i], i);
                }
            }
        return "";
    }
    public static String findComplement(int[] ar, int num) {
        for ( int i = 0; i < ar.length; i++ ) {
            for ( int j = i + 1; j < ar.length; j++ ) {
                if ( ar[i] + ar[j] == num ) {
                    return i + ", " + j;
                }
            }
        }
        return "";
    }

    public static String twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return nums[i] + ", " + map.get(complement);
            } else {
                map.put(nums[i], i);
            }
        }
        return "";
    }

    public static String onSorted(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        for (int i = 0; i < nums.length; i++) {
            int result = nums[left] + nums[right];
            if (result == target) {
                return nums[left] + ", " + nums[right];
            } else if (result < target) {
                left++;
            } else {
                right--;
            }
        }
        return "";
    }


    public static void main(String[] args) {
//        System.out.println(twoSum(new int[]{3, 5, 2, 1}, 5));
        System.out.println(onSorted(new int[]{1, 2, 3, 5}, 5));
    }
}
