package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.blocking_noneBlocking;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * NON-BLOCKING: poll() returns immediately even if nothing is there yet (null) -
 * "Doing more work" prints right away, before data ever arrives.
 */
public class NonBlockingDemo {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

        new Thread(() -> {
            try {
                Thread.sleep(2000); // simulate something arriving late
                queue.put("data");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        System.out.println("Calling poll() - returns immediately, no waiting...");
        String result = queue.poll(); // NON-BLOCKING, returns null if nothing is ready yet
        System.out.println("Got: " + result);
        System.out.println("Doing more work");
    }
}
