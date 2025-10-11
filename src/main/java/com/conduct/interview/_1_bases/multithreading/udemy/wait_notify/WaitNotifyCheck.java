package com.conduct.interview._1_bases.multithreading.udemy.wait_notify;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class WaitNotifyCheck {
    public static void main(String[] args) {

        Object lock = new Object();
        Queue<String> queue = new ArrayBlockingQueue<>(5);

        Thread thread1 = new Thread(() -> {
            try {
                synchronized (queue) {
                    while(true) {
                        if (queue.isEmpty()) {
                            queue.notify();
                        }
                        if (!queue.isEmpty()) {
                            System.out.println("Handling" + queue.poll());
                        } else {
                            queue.wait();
                        }

                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread thread2 = new Thread(() -> {
            synchronized (queue) {
                while(true) {
                    if (queue.size() == 5) {
                            queue.notify();
                    }
                        if (queue.size() < 5) {
                            queue.add("item1");
                        } else {
                            try {
                                queue.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                }
            }
        });

        thread1.start();
        thread2.start();
    }
}
