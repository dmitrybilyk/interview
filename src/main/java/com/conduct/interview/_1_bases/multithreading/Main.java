package com.conduct.interview._1_bases.multithreading;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        long start = System.currentTimeMillis();

//        long start = System.currentTimeMillis();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        MyThread1 myThread1 = new MyThread1();
        myThread1.start();

        MyThread2 myThread2 = new MyThread2();
        myThread2.start();

//        myThread2.join();
//        myThread1.join();

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Result - " + timeElapsed);

        System.out.println("next action");

    }
}

class MyThread1 extends Thread {
    @Override
    public void run() {
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 700000000; i++) {
//            int a = 2 + i;
//        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        long finish = System.currentTimeMillis();
//        long timeElapsed = finish - start;
//        System.out.println(timeElapsed);
    }
}

class MyThread2 extends Thread {
    @Override
    public void run() {
//        long start2 = System.currentTimeMillis();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        long finish2 = System.currentTimeMillis();
//        long timeElapsed2 = finish2 - start2;
//        System.out.println(timeElapsed2);
    }
}

