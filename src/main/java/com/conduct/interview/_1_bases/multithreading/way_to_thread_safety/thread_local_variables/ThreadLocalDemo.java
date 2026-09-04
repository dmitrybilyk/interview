package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.thread_local_variables;

/**
 * ThreadLocal gives each thread its own private copy of a variable - no sharing, so no race,
 * even though every thread uses the same "counter" field.
 */
public class ThreadLocalDemo {

    private static final ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> 0);

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 3; i++) {
                counter.set(counter.get() + 1);
                System.out.println(Thread.currentThread().getName() + " sees: " + counter.get());
            }
        };

        Thread t1 = new Thread(task, "Thread-A");
        Thread t2 = new Thread(task, "Thread-B");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // Both threads count 1,2,3 independently - they never see each other's value.
    }
}
