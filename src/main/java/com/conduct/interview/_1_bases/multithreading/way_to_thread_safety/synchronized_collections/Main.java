package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.synchronized_collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Main {
  public static void main(String[] args) {
    Collection<Integer> syncCollection = Collections.synchronizedCollection(new ArrayList<>());
    Thread thread1 = new Thread(() -> syncCollection.addAll(Arrays.asList(1, 2, 3, 4, 5, 6)));
    Thread thread2 = new Thread(() -> syncCollection.addAll(Arrays.asList(7, 8, 9, 10, 11, 12)));
    thread1.start();
    thread2.start();
  }
}
