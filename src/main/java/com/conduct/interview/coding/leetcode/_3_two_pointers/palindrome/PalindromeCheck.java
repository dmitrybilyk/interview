package com.conduct.interview.coding.leetcode._3_two_pointers.palindrome;

public class PalindromeCheck {
    public static void main(String[] args) {
        System.out.println(palindromeCheck("douod"));
    }

    private static boolean palindromeCheck(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left++) != s.charAt(right--)) {
                return false;
            }
        }
        return true;
    }
}
