package com.conduct.interview._1_bases.multithreading.producer_consumer.wait_notify.simple;

import java.util.stream.IntStream;

public class WaitNotifyTest {

    public static void main(String[] args) {
//        Message msg = new Message("process it");
//        Waiter waiter = new Waiter(msg);
//        new Thread(waiter, "waiter").start();
//
//        Waiter waiter1 = new Waiter(msg);
//        new Thread(waiter1, "waiter1").start();
//
//        Notifier notifier = new Notifier(msg);
//        new Thread(notifier, "notifier").start();
//        System.out.println("All the threads are started");

        Message message = new Message("process it");
        IntStream.range(1, 10).forEach(value ->
        {
            Waiter theWaiter = new Waiter(message);
            new Thread(theWaiter, "the waiter").start();
        });
        Notifier theNotifier = new Notifier(message);
        new Thread(theNotifier).start();
        theNotifier.notifyAll();
    }

}