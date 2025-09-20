package com.conduct.interview.coding;

public class Fac {
    public static void main(String[] args) {
        System.out.println(calculateFactorial(5));
    }

    private static int calculateFactorial(int key) {
        if (key == 1) {
            return 1;
        }
        return key * calculateFactorial(key - 1);
    }
}
