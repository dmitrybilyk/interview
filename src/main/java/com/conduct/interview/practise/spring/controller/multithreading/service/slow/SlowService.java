package com.conduct.interview.practise.spring.controller.multithreading.service.slow;

import org.springframework.stereotype.Service;

@Service
public class SlowService {

    /** Simulate a slow blocking task */
    public String slowTask(int taskId) {
        try {
            System.out.println("Task " + taskId + " started in thread " + Thread.currentThread().getName());
            Thread.sleep(3000); // simulate 3 seconds of blocking
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Task " + taskId + " finished in thread " + Thread.currentThread().getName());
        return "Task " + taskId + " done";
    }

    /** Run multiple tasks sequentially */
    public String runSequentialTasks(int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            result.append(slowTask(i)).append("\n");
        }
        return result.toString();
    }
}
