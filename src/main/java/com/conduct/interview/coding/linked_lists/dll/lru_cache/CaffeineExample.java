//package com.conduct.interview.coding.linked_lists.dll.lru_cache;
//
//
//import java.util.concurrent.TimeUnit;
//
//public class CaffeineExample {
//    public static void main(String[] args) {
//        // Build the cache
//        Cache<Integer, Integer> cache = Caffeine.newBuilder()
//                .maximumSize(5) // LRU-style capacity
//                .expireAfterWrite(10, TimeUnit.MINUTES) // Optional bonus: TTL
//                .build();
//
//        // Threads can call these concurrently without any extra locking
//
//        // Put a value
//        cache.put(1, 100);
//
//        // Get a value (returns null if not present)
//        Integer value = cache.getIfPresent(1);
//
//        // The "Smart" Get: Compute value if it's missing (atomic)
//        Integer autoValue = cache.get(2, key -> key * 10);
//
//        System.out.println("Finished! Caffeine handles all the locking internally.");
//    }
//}