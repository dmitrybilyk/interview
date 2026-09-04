package com.conduct.interview._1_bases.multithreading.thread_lifecycle;

/**
 * Walks through NEW -> TIMED_WAITING -> BLOCKED -> TERMINATED using Thread.getState().
 */
public class ThreadLifecycleDemo {

    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();

        Thread worker = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(1000); // holds the lock while sleeping
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        System.out.println("Before start():     " + worker.getState()); // NEW

        worker.start();
        Thread.sleep(100);
        System.out.println("While sleeping:      " + worker.getState()); // TIMED_WAITING

        Thread blocked = new Thread(() -> {
            synchronized (lock) {
                // can't enter until "worker" releases the lock above
            }
        });
        blocked.start();
        Thread.sleep(100);
        System.out.println("Waiting for lock:    " + blocked.getState()); // BLOCKED

        worker.join();
        blocked.join();
        System.out.println("After both finish:   " + worker.getState()); // TERMINATED
    }
}
