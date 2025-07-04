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

    public static void main(String[] args) {
        System.out.println(findComplementMap(new int[]{3, 5, 2, 1}, 5));
    }
}
