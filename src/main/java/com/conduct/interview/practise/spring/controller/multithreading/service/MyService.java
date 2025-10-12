package com.conduct.interview.practise.spring.controller.multithreading.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class MyService {
    private List<Integer> transactionIds = new ArrayList<>();
    private int nextId = 1; // Counter for generating IDs
    private final ReentrantLock lock = new ReentrantLock();

    public String sayHello() {
        log.info("In MyService thread name is - {}", Thread.currentThread().getName());
        return "Hello World!";
    }

    public void incrementCounter() {
        // No lock to trigger data race
        int id = nextId; // Read shared counter
        try {
            Thread.sleep(1); // Increase race window
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        nextId++; // Increment shared counter
        transactionIds.add(id); // Add to shared list
        log.info("Added ID: {}, List size: {}, List: {}", id, transactionIds.size(), transactionIds);
    }

    public void reset() {
        transactionIds.clear();
        nextId = 1;
        log.info("Reset: transactionIds cleared, nextId=1");
    }

    public String getStatus() {
        return "List size: " + transactionIds.size() + ", List: " + transactionIds;
    }

}
