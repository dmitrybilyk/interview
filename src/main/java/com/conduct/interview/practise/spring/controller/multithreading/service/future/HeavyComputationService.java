package com.conduct.interview.practise.spring.controller.multithreading.service.future;

import org.springframework.stereotype.Service;

@Service
public class HeavyComputationService {

    /** Simple check for primality */
    private boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    /** Count primes below 'limit' — heavy CPU work */
    private long countPrimes(int limit) {
        long count = 0;
        for (int i = 2; i < limit; i++) {
            if (isPrime(i)) count++;
        }
        return count;
    }

    /** Run sequentially */
    public String runSequential(int tasks, int limit) {
        long start = System.currentTimeMillis();

        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= tasks; i++) {
            long primes = countPrimes(limit);
            result.append("Task ").append(i)
                  .append(" → primes below ").append(limit)
                  .append(" = ").append(primes)
                  .append("\n");
        }

        long time = System.currentTimeMillis() - start;
        return result + "\nTotal time: " + time + " ms";
    }
}
