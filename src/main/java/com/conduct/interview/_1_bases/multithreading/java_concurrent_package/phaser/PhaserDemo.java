package com.conduct.interview._1_bases.multithreading.java_concurrent_package.phaser;

import java.util.concurrent.Phaser;

/**
 * Like CyclicBarrier but for multiple phases, with parties that can register/deregister
 * at runtime instead of being fixed at construction time.
 */
public class PhaserDemo {

    public static void main(String[] args) {
        Phaser phaser = new Phaser(3); // 3 parties registered up front

        for (int i = 1; i <= 3; i++) {
            int workerId = i;
            new Thread(() -> {
                System.out.println("Worker " + workerId + " phase 1");
                phaser.arriveAndAwaitAdvance(); // wait for all 3 to finish phase 1

                System.out.println("Worker " + workerId + " phase 2");
                phaser.arriveAndAwaitAdvance(); // wait for all 3 to finish phase 2

                System.out.println("Worker " + workerId + " done");
                phaser.arriveAndDeregister();
            }).start();
        }
    }
}
