package com.conduct.interview.coding.lru_cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThreadSafeLruCache<K, V> {

    private final Map<K, V> cache;

    public ThreadSafeLruCache(int capacity) {
        // LinkedHashMap with accessOrder=true for LRU
        LinkedHashMap<K, V> map = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };

        // wrap in synchronized map for thread safety
        this.cache = Collections.synchronizedMap(map);
    }

    public V get(K key) {
        return cache.get(key); // synchronized internally
    }

    public void put(K key, V value) {
        cache.put(key, value); // synchronized internally
    }

    @Override
    public String toString() {
        synchronized (cache) { // need to synchronize when iterating
            return cache.toString();
        }
    }

    public static void main(String[] args) {
        ThreadSafeLruCache<Integer, String> cache = new ThreadSafeLruCache<>(2);

        cache.put(1, "one");
        cache.put(2, "two");
        System.out.println(cache); // {1=one, 2=two}

        cache.get(1);               // Access 1 → moves it to front
        cache.put(3, "three");      // Evict least recently used (2)
        System.out.println(cache); // {1=one, 3=three}
    }
}
