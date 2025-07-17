package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.concurrent_collections;

import java.util.concurrent.ArrayBlockingQueue;

public class WithArrayBlockingQueue {
    public static void main(String[] args) {
        ArrayBlockingQueue<Integer> arrayBlockingQueue = new ArrayBlockingQueue<>(5);

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                    arrayBlockingQueue.put(i);
                    System.out.println("Produced - " + i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumer = new Thread(() -> {
           for (int i = 0; i < 5; i++) {
               try {
                   Thread.sleep(200);
                   int took = arrayBlockingQueue.take();
                   System.out.println("Consumed - " + took);
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
               }
           }
        });

        producer.start();
        consumer.start();


    }
}
