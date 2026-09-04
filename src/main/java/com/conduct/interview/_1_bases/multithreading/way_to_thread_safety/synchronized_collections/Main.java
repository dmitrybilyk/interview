package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.synchronized_collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Collections.synchronizedXxx() wraps every method with a lock on the same object -
 * correct, but only one thread can touch the collection at a time (no segmenting like
 * ConcurrentHashMap does), so it's a bottleneck under heavy concurrent use.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Collection<Integer> list = Collections.synchronizedCollection(new ArrayList<>());

        Thread t1 = new Thread(() -> list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6)));
        Thread t2 = new Thread(() -> list.addAll(Arrays.asList(7, 8, 9, 10, 11, 12)));
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Expected size: 12");
        System.out.println("Actual size:   " + list.size()); // always correct
    }
}
