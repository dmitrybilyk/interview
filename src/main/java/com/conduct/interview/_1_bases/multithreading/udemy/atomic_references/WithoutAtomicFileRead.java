package com.conduct.interview._1_bases.multithreading.udemy.atomic_references;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WithoutAtomicFileRead {
    private static String lastLineRead = null;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> readFile("Thread-1"));
        Thread t2 = new Thread(() -> readFile("Thread-2"));

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final last line (without atomic): " + lastLineRead);
    }

    private static void readFile(String threadName) {
        try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Simulate some blocking delay
                Thread.sleep(10);
                lastLineRead = line; // ❌ not thread-safe
                System.out.println(threadName + " read: " + line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
