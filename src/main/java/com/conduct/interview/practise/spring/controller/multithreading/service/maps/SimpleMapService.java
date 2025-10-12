package com.conduct.interview.practise.spring.controller.multithreading.service.maps;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SimpleMapService {

    private final Map<Integer, String> map = new HashMap<>();

    /** Add key-value pairs */
    public void putItems(int items) {
        for (int i = 0; i < items; i++) {
            map.put(i, "Value " + i); // Not thread-safe!
        }
    }

    public int getSize() {
        return map.size();
    }

    public void clear() {
        map.clear();
    }

    public Map<Integer, String> getMap() {
        return map;
    }
}
