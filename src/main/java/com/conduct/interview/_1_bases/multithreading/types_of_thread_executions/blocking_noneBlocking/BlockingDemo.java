package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.blocking_noneBlocking;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * BLOCKING: take() parks the calling thread until an item is available -
 * "Doing more work" only prints after something is put into the queue.
 */
public class BlockingDemo {

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

        System.out.println("Calling take() - thread stops here until data shows up...");
        String result = queue.take(); // BLOCKS for ~2s
        System.out.println("Got: " + result);
        System.out.println("Doing more work");
    }
}
