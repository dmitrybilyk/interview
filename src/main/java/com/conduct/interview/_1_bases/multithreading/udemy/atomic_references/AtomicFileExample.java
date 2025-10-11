package com.conduct.interview._1_bases.multithreading.udemy.atomic_references;

import java.io.*;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicFileExample {

    private static final String FILE_NAME = "data.txt";
    private static final AtomicReference<String> lastLineRead = new AtomicReference<>(null);

    public static void main(String[] args) throws Exception {
        createFile(); // 1️⃣ create data.txt if not exists

        Thread t1 = new Thread(() -> readFile("Thread-1"));
        Thread t2 = new Thread(() -> readFile("Thread-2"));

        // 2️⃣ start both threads
        t1.start();
        t2.start();

        // 3️⃣ wait for both to finish
        t1.join();
        t2.join();

        // 4️⃣ print result
        System.out.println("\n✅ Final last line (with atomic): " + lastLineRead.get());
    }

    private static void createFile() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            System.out.println("File already exists, skipping creation...");
            return;
        }

        System.out.println("Creating " + FILE_NAME + "...");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 1; i <= 5; i++) {
                writer.write("Line " + i);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating file", e);
        }
    }

    private static void readFile(String threadName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Simulate blocking operation
                Thread.sleep(10);
                lastLineRead.set(line); // ✅ atomic write
                System.out.println(threadName + " read: " + line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
