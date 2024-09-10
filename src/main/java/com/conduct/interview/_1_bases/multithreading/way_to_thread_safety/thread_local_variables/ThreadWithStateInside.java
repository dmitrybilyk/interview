package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.thread_local_variables;

import java.util.Arrays;
import java.util.List;

public class ThreadWithStateInside extends Thread {
    
    private final List<String> letters = Arrays.asList("a", "b", "c", "d", "e", "f");
    
    @Override
    public void run() {
        letters.forEach(System.out::println);
    }
}