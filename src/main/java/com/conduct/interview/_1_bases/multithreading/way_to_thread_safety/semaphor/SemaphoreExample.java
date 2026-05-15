package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.semaphor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreExample {

    public static void main(String[] args) {
        // Створюємо семафор з 3 дозволами (permits). 
        // Тільки 3 потоки зможуть пройти через нього одночасно.
        Semaphore semaphore = new Semaphore(3);

        // Пул з 10 потоків, які будуть намагатися "штурмувати" наш ресурс
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            executor.execute(() -> {
                try {
                    System.out.println("Потік " + taskId + " чекає на дозвіл...");
                    
                    // Блокуємося тут, якщо лічильник семафора = 0
                    semaphore.acquire();
                    
                    try {
                        System.out.println(">>> Потік " + taskId + " ОТРИМАВ ДОЗВІЛ і працює");
                        // Імітація роботи (наприклад, важкий HTTP-запит)
                        Thread.sleep(2000); 
                    } finally {
                        System.out.println("<<< Потік " + taskId + " закінчив і звільняє місце");
                        // Обов'язково звільняємо семафор у блоці finally!
                        semaphore.release();
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
    }
}