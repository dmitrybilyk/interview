package com.conduct.interview.coding;

import java.util.HashMap;
import java.util.Map;

public class FindSumPart {
    public static void main(String[] args) {
        int[] arr = new int[]{3, 6, 1, 8, 3};
        int[] arrSorted = new int[]{1, 4, 7, 8, 10};
        System.out.println(findSumPart(arr, 13));
        System.out.println(findSumPartIterative(arr, 11));
//        System.out.println(findSumPartAfterSorting(arr, 16));
    }

    // O(n), time and space complexity
    private static boolean findSumPart(int[] arr, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < arr.length - 1; i++) {
            int dif = k - arr[i];
            Integer value = map.get(dif);
            if (value != null) {
                return true;
            } else {
                map.put(arr[i], arr[i]);
            }
        }
        return false;
    }

//    O(n2) - time complexity and space is O(1)
    private static boolean findSumPartIterative(int[] arr, int k) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length -1; j++) {
                if (arr[i] + arr[j] == k) {
                    return true;
                }
            }
        }
        return false;
    }

//    O(logn) - because need to sort before after every iteration loop is decreased in size. for the space the same
//    need to sort first
    private static boolean findSumPartAfterSorting(int[] arr, int k) {
        int start = 0;
        int end = arr.length - 1;
        while (start < end) {
            int sum = arr[start] + arr[end];
            if (sum == k) {
                return true;
            } else if (sum > k) {
                start++;
            } else {
                end--;
            }
        }
        return false;
    }
}
