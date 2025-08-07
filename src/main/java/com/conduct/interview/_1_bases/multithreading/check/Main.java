package com.conduct.interview._1_bases.multithreading.check;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MyService myService = new MyService();

//        Thread t1 = new Thread(() -> {
//            System.out.println("Thread 1");
//        });
//        t1.start();

        Thread t2 = new Thread(new MyRunnable(myService));
        Thread t3 = new Thread(new MyRunnable2(myService));
        t2.start();
        t3.start();

        t2.join();
        t3.join();

        System.out.println("Counter value - " + myService.getCounter());

//        System.out.println("In main");

//        try (ExecutorService executorService = Executors.newFixedThreadPool(5)) {
//            executorService.execute(new MyRunnable());
//            executorService.submit(new MyRunnable());
//        }
    }

}

class MyRunnable implements Runnable {
    private MyService myService;
    public MyRunnable(MyService myService) {
        this.myService = myService;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            if (i == 500) {
                myService.setCounter(new AtomicInteger());
            }
            myService.incrementCounter();
        }
        myService.doSomething();
        System.out.println("Thread 1");
    }
}

class MyRunnable2 implements Runnable {
    private MyService myService;
    public MyRunnable2(MyService myService) {
        this.myService = myService;
    }

    @Override
    public void run() {
        System.out.println("current - " + myService.getCounter());
        for (int i = 0; i < 1000; i++) {
            myService.incrementCounter();
        }
        myService.doSomething();
        System.out.println("Thread 1");
    }
}

class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("fdfd");
    }
}

class MyService {
    private AtomicInteger counter = new AtomicInteger();

    public int getCounter() {
        return counter.get();
    }

    public void setCounter(AtomicInteger counter) {
        this.counter = counter;
    }


    public void incrementCounter() {
        counter.incrementAndGet();
    }

    public void doSomething() {
        System.out.println("MyService and counter - " + counter);
    }
}
