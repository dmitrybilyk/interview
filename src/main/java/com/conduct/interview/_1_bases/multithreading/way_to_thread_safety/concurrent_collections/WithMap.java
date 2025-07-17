package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.concurrent_collections;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class WithMap {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        Runnable counter = () -> {
            for (int i = 0; i < 1000; i++) {
                String key = "key" + i % 10;
                map.merge(key, 1, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                });
            }
        };

        Thread counterThread = new Thread(counter);
        Thread counterThread2 = new Thread(counter);
        Thread counterThread3 = new Thread(counter);

        counterThread.start();
        counterThread2.start();
        counterThread3.start();

        counterThread.join();
        counterThread2.join();
        counterThread3.join();

    }
}
