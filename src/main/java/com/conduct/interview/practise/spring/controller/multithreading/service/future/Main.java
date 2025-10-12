package com.conduct.interview.practise.spring.controller.multithreading.service.future;

public class Main {
    public static void main(String[] args) {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println(cores);
    }
}
