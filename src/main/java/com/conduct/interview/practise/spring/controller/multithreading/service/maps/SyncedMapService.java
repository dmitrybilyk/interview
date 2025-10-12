package com.conduct.interview.practise.spring.controller.multithreading.service.maps;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SyncedMapService {

    private final Map<Integer, String> syncMap = Collections.synchronizedMap(new HashMap<>());

    /** Add key-value pairs */
    public void putItems(int items) {
        for (int i = 0; i < items; i++) {
            syncMap.put(i, "Value " + i); // Thread-safe
        }
    }

    public int getSize() {
        return syncMap.size();
    }

    public void clear() {
        syncMap.clear();
    }

    public Map<Integer, String> getMap() {
        return syncMap;
    }
}
