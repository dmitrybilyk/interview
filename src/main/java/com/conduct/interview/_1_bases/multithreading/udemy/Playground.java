//package com.conduct.interview._1_bases.multithreading.udemy;
//
//import com.conduct.interview._1_bases.generics.udemy.Student;
//
//public class Playground {
//    public static void main(String[] args) {
//        // Shared counter object
//        Counter counter = new Counter();
//
//        // Two threads incrementing the counter
//        Thread thread1 = new Thread(() -> {
//            for (int i = 0; i < 100_000; i++) {
//                counter.increment();
//            }
//            System.out.println("Thread-1 finished, counter = " + counter.getCount());
//        });
//
//        Thread thread2 = new Thread(() -> {
//            for (int i = 0; i < 100_000; i++) {
//                counter.increment();
//            }
//            System.out.println("Thread-2 finished, counter = " + counter.getCount());
//        });
//
//        // Start both threads
//        thread1.start();
//        thread2.start();
//
//        // Wait for both threads to finish
//        try {
//            thread1.join();
//            thread2.join();
//        } catch (InterruptedException e) {
//            System.out.println("Main thread interrupted: " + e.getMessage());
//        }
//
//        // Print final result
//        System.out.println("Final counter value: " + counter.getCount());
//    }
//}
//
//class Counter {
//    private Object object = new Object();
//    private Student student = Student.builder().build();
//    private int count = 0;
//
//    public void increment() {
//        synchronized (student) {
//            count++; // Non-atomic operation: read, increment, write
//        }
//    }
//
//    public int getCount() {
//        return count;
//    }
//}