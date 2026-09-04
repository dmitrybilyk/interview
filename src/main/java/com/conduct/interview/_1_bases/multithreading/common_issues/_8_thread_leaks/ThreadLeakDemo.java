package com.conduct.interview._1_bases.multithreading.common_issues._8_thread_leaks;

/**
 * BAD PRACTICE: a raw thread is spawned per "request" and never terminates,
 * so nothing is ever reclaimed. Watch the active thread count only grow.
 */
public class ThreadLeakDemo {

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            new Thread(() -> {
                try {
                    Thread.sleep(Long.MAX_VALUE); // never completes - the leak
                } catch (InterruptedException ignored) {
                }
            }).start();

            System.out.println("Active Threads: " + Thread.activeCount());
            Thread.sleep(100);
        }
    }
}
