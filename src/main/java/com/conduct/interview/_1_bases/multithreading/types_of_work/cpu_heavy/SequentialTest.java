package com.conduct.interview._1_bases.multithreading.types_of_work.cpu_heavy;

import java.util.stream.LongStream;

public class SequentialTest {
    public static void main(String[] args) {
        long limit = 10_000_000; 
        System.out.println("--- Sequential Mode ---");
        
        long start = System.currentTimeMillis();
        
        long count = LongStream.rangeClosed(2, limit)
                .filter(SequentialTest::isPrime)
                .count();
        
        long end = System.currentTimeMillis();
        
        System.out.println("Time taken: " + (end - start) + " ms");
        System.out.println("Primes found: " + count);
    }

    private static boolean isPrime(long n) {
        if (n <= 1) return false;
        for (long i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}