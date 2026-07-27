package com.conduct.interview.coding.factorial;

import java.math.BigInteger;

public class FactorialCheck3 {
    public static void main(String[] args) {
        System.out.println(factorial3(21));
        System.out.println(recursiveFactorial3(21));
    }

    private static BigInteger factorial3(int source) {
        if (source <= 1) {
            return BigInteger.ONE;
        }
        BigInteger factorial = BigInteger.ONE;

            for (int i = 1; i <= source; i++) {
                factorial = factorial.multiply(BigInteger.valueOf(i));
            }
        return factorial;
    }

    private static BigInteger recursiveFactorial3(int source) {
        if (source <= 1) {
            return BigInteger.ONE;
        } else {
            return BigInteger.valueOf(source).multiply(recursiveFactorial3(source - 1));
        }
    }
}
