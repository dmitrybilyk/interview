package com.conduct.interview.coding.linked_lists.dll.lru_cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapLRU {
    public static void main(String[] args) {
        final int capacity = 5;

        // 1. Create a LinkedHashMap with 'accessOrder' set to true
        // 2. Wrap it in a synchronized map to make it thread-safe
        Map<Integer, Integer> cache = Collections.synchronizedMap(
            new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                    // This is called automatically after every put/putAll
                    return size() > capacity;
                }
            }
        );

        // Usage is just like a normal Map
        cache.put(1, 100);
        cache.get(1); // Moves key 1 to the 'Most Recently Used' position
    }
}