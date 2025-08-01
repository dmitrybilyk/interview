package com.conduct.interview.leetcode;

public class Palindrome {
    public static void main(String[] args) {
        System.out.println(isPallindrome("poop"));
    }

    private static boolean isPallindrome(String source) {
        int left = 0, right = source.length() - 1;
        while(left < right) {
            if(source.charAt(left++) != source.charAt(right--)) {
                return false;
            }
        }
        return true;
    }
}
