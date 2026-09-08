package com.conduct.interview.coding.leetcode._3_two_pointers;

// LC 125 — Valid Palindrome (спрощена версія без фільтрації символів)
// Задача: перевірити, чи рядок читається однаково зліва направо і справа наліво.
// Ідея two-pointer: два вказівники сходяться з обох країв до середини.
// Час O(n), Пам'ять O(1)
public class Palindrome {
    public static void main(String[] args) {
        System.out.println(isPallindrome("poop"));
    }

    private static boolean isPallindrome(String source) {
        int left = 0, right = source.length() - 1;
        // left йде від початку, right — від кінця рядка

        while(left < right) {
            if(source.charAt(left++) != source.charAt(right--)) {
                return false;
                // символи не збігаються -> це не паліндром
                // (left++/right-- зсувають вказівники одразу після порівняння)
            }
        }
        return true;
        // вказівники зустрілись, а всі пари символів збігались -> паліндром
    }
}
