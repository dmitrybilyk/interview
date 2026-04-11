package com.conduct.interview._1_bases.multithreading.common_issues.memory_consistency_volatile;

/**
 * Memory Consistency / Visibility Demo
 * * To see the failure: Run this without 'volatile'.
 * To see the fix: Add 'volatile' to the ready variable.
 */
public class VisibilityDemo {

    // WITHOUT volatile: The reader thread may never see the change.
    // WITH volatile: The change is guaranteed to be visible across threads.
//    private static boolean ready = false;
    private static volatile boolean ready = false;

    public static void main(String[] args) throws InterruptedException {
        
        // 1. Reader Thread
        new Thread(() -> {
            System.out.println("Reader: Waiting for ready flag...");
            while (!ready) {
                // The JIT compiler optimizes this to: if (!ready) while(true) {}
                // It doesn't check main memory every loop iteration.
            }
            System.out.println("Reader: Finally saw the change! Exiting...");
        }).start();

        // Give the reader a head start to cache the 'false' value
        Thread.sleep(500);

        // 2. Writer Thread
        new Thread(() -> {
            System.out.println("Writer: Changing ready flag to true...");
            ready = true;
            System.out.println("Writer: Change complete.");
        }).start();
    }
}