package com.conduct.interview.practise.spring.controller.multithreading.service.future;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class FutureComputationService {

    private final ExecutorService pool;

    public FutureComputationService() {
        int cores = Runtime.getRuntime().availableProcessors();
        pool = Executors.newFixedThreadPool(cores);
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    private long countPrimes(int limit) {
        long count = 0;
        for (int i = 2; i < limit; i++) {
            if (isPrime(i)) count++;
        }
        return count;
    }

    public String runParallel(int tasks, int limit) throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();

        List<Future<Long>> futures = new ArrayList<>();
        for (int i = 1; i <= tasks; i++) {
            int id = i;
            futures.add(pool.submit(() -> {
                System.out.println("Task " + id + " running in " + Thread.currentThread().getName());
                return countPrimes(limit);
            }));
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < futures.size(); i++) {
            long primes = futures.get(i).get();
            result.append("Task ").append(i + 1)
                  .append(" → primes below ").append(limit)
                  .append(" = ").append(primes)
                  .append("\n");
        }

        long time = System.currentTimeMillis() - start;
        return result + "\nTotal time: " + time + " ms";
    }
}
