package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.volatile_keyword;

/**
 * FIX for the visibility bug shown in common_issues/_6_memory_consistency_volatile/VisibilityDemo:
 * marking "ready" volatile guarantees every thread reads it from main memory, not a cached copy -
 * so the reader reliably sees it flip to true instead of possibly spinning forever.
 */
public class VolatileDemo {

    private static volatile boolean ready = false;
    private static int number;

    public static void main(String[] args) throws InterruptedException {
        Thread reader = new Thread(() -> {
            System.out.println("Reader: waiting for ready flag...");
            while (!ready) {
                // busy-wait
            }
            System.out.println("Reader: saw ready=true, number=" + number);
        });
        reader.start();

        Thread.sleep(500); // give the reader a head start

        number = 42;
        ready = true; // writing a volatile also flushes "number", written before it, to main memory
        System.out.println("Writer: published number and ready");
    }
}
