package com.conduct.interview.practise.spring.controller.multithreading.service.maps;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConcurrentMapService {

    private final Map<Integer, String> concurrentMap = new ConcurrentHashMap<>();

    /** Add key-value pairs concurrently — fully thread-safe */
    public void putItems(int items) {
        for (int i = 0; i < items; i++) {
            concurrentMap.put(i, "Value " + i); // Thread-safe, lock-free for many writes
        }
    }

    public int getSize() {
        return concurrentMap.size();
    }

    public void clear() {
        concurrentMap.clear();
    }

    public Map<Integer, String> getMap() {
        return concurrentMap;
    }
}
