package com.conduct.interview._1_bases.multithreading.common_issues.context_switch;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Realistic Context Switching Demo (Cache Thrashing)
 */
public class ContextSwitchThrasher {
    private static final int THREAD_COUNT = 1000; // More than your 8 logical cores
    private static final int ARRAY_SIZE = 64 * 1024; // 64KB to fill L1/L2 cache
    private static final AtomicLong totalOperations = new AtomicLong(0);

    public static void main(String[] args) {
        System.out.println("Starting Cache-Thrashing Context Switch Demo...");

        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                // Each thread has its own "data" to force cache misses on switch
                int[] data = new int[ARRAY_SIZE];
                while (true) {
                    // Simulating a small task that uses memory
                    for (int j = 0; j < 100; j++) {
                        data[j % ARRAY_SIZE] ++;
                    }
                    totalOperations.incrementAndGet();
                    
                    // Instead of yield, we use a tiny sleep. 
                    // This forces the OS to put the thread in a WAITING state 
                    // and schedule a different thread.
                    try {
                        Thread.sleep(1); 
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }).start();
        }

        // Monitor throughput
        new Thread(() -> {
            while (true) {
                long before = totalOperations.get();
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                long after = totalOperations.get();
                System.out.printf("Completed Tasks/sec: %,d%n", (after - before));
            }
        }).start();
    }
}