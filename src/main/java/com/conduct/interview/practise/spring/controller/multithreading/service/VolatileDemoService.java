package com.conduct.interview.practise.spring.controller.multithreading.service;

import org.springframework.stereotype.Service;

@Service
public class VolatileDemoService {

    private boolean running = true; // try without volatile first

    public void startTask() {
        System.out.println("Starting long-running task...");

        Thread worker = new Thread(() -> {
            long counter = 0;
            while (running) {
                counter++;
                // Do something small to avoid being optimized out
                if (counter % 1_000_000_000 == 0) {
                    System.out.println("Still running... " + counter);
                }
            }
            System.out.println("Worker stopped at: " + counter);
        }, "WorkerThread");

        worker.start();

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("Requesting stop...");
                running = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "StopperThread").start();
    }
}
