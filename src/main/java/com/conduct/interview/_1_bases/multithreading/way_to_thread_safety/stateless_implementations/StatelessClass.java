package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.stateless_implementations;

import java.math.BigInteger;

/**
 * Stateless: no fields at all, result depends only on the input. Nothing is shared between
 * calls, so any number of threads can call this at once with zero risk of a race.
 */
public class StatelessClass {

    public static BigInteger factorial(int number) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= number; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> System.out.println("5! = " + factorial(5)));
        Thread t2 = new Thread(() -> System.out.println("10! = " + factorial(10)));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // Both calls are correct regardless of interleaving - there's no shared state to corrupt.
    }
}
