package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.double_checked_locking;

/**
 * Lazy singleton, safe under concurrent first access, without locking on every call:
 * 1. check without a lock (fast path - once created, no locking cost ever again)
 * 2. only lock if it might still be null
 * 3. check again inside the lock (another thread may have created it while we were waiting)
 */
public class LazySingleton {

    private static volatile LazySingleton instance; // volatile is required, see the note below

    private LazySingleton() {
    }

    public static LazySingleton getInstance() {
        if (instance == null) {
            synchronized (LazySingleton.class) {
                if (instance == null) {
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () ->
                System.out.println(Thread.currentThread().getName() + " got " + LazySingleton.getInstance());

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // Both threads print the exact same instance (same hash code) - only one was ever created.
    }
}
