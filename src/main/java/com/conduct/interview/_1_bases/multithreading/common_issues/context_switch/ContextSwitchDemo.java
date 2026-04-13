package com.conduct.interview._1_bases.multithreading.common_issues.context_switch;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Context Switching Demo
 * * We spawn a massive number of threads (e.g., 2000+) to fight over 
 * a limited number of CPU cores.
 */
public class ContextSwitchDemo {
    private static final int THREAD_COUNT = 2000;
    private static final AtomicLong totalWork = new AtomicLong(0);

    public static void main(String[] args) {
        System.out.println("Starting " + THREAD_COUNT + " threads...");
        System.out.println("Watch your CPU usage in Task Manager/HTOP!");

        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                while (true) {
                    // Perform a tiny bit of work
                    totalWork.incrementAndGet();

                    // FORCE a context switch: 
                    // Thread.yield() hints to the OS that this thread is 
                    // willing to give up its current use of a processor.
                    Thread.yield();
                }
            }).start();
        }

//        // Monitoring thread to show progress
//        new Thread(() -> {
//            while (true) {
//                long startWork = totalWork.get();
//                try { Thread.sleep(1000); } catch (InterruptedException e) {}
//                long endWork = totalWork.get();
//
//                // This shows how many operations we are actually completing per second
//                System.out.printf("Operations per second: %,d%n", (endWork - startWork));
//            }
//        }).start();
    }
}