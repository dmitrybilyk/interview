package com.conduct.interview._1_bases.multithreading.common_issues.starvation;

public class StarvationDemo {

    public static void main(String[] args) {
        // 1. The "Snail" - Lowest priority
        Thread snail = new Thread(() -> runTask("SNAIL (Low Priority)"), "Snail-Thread");
        snail.setPriority(Thread.MIN_PRIORITY);

        // 2. The "Rabbits" - Highest priority
        // We create several to ensure they keep the CPU busy
        for (int i = 1; i <= 5; i++) {
            Thread rabbit = new Thread(() -> runTask("RABBIT (High Priority)"), "Rabbit-Thread-" + i);
            rabbit.setPriority(Thread.MAX_PRIORITY);
            rabbit.start();
        }

        // Start the snail last
        snail.start();
    }

    private static void runTask(String name) {
        long count = 0;
        while (true) {
            count++;
            // We print every 10 million iterations so we don't flood the console
            if (count % 10_000_000 == 0) {
                System.out.println(name + " is working... iteration: " + count);
            }
        }
    }
}