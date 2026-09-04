package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.immutable_implementations;

/**
 * Immutable: state is final and never changes after construction, so it's automatically
 * thread-safe - no lock needed, since there's nothing for threads to race over.
 */
public class MessageService {

    private final String message;

    public MessageService(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static void main(String[] args) throws InterruptedException {
        MessageService shared = new MessageService("hello");

        Runnable task = () -> System.out.println(Thread.currentThread().getName() + " read: " + shared.getMessage());

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // No synchronization anywhere above, and no race is possible - message can never change.
    }
}
