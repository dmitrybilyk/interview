package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.atomic_objects;

/**
 * UNSAFE: counter++ is read-modify-write, not atomic - two threads doing it 5000 times each
 * should give 10000, but concurrent updates get lost, so the actual result is usually less.
 */
public class Counter {

    private int counter = 0;

    public void increment() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        Runnable task = () -> {
            for (int i = 0; i < 5000; i++) {
                counter.increment();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Expected: 10000");
        System.out.println("Actual:   " + counter.getCounter());
    }
}
