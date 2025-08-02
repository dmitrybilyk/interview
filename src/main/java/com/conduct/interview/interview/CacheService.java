//package com.conduct.interview.interview;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
//// What you see is a piece of code which you've got to code review.
//// Basically this is a caching layer on top of a costly / heavy-computation external service.
//// Please review it and then propose and implement required improvements.
//
//
//public class CacheService {
//
//    private final ConcurrentHashMap<List<String>, List<String>> cache = new ConcurrentHashMap<>();
//
//    public List<String> get(String[] key) {
//            List<String> actualKey = List.of(key);
//            return cache.computeIfAbsent(actualKey, k -> externalService.get());
//
//    }
//}