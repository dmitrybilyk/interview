package com.conduct.interview.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class Digest {
 
    private Map<byte[], byte[]> cache = new ConcurrentHashMap<>() {
    };
    private Object lock = new Object();
 
    public byte[] digest(byte[] input) {
        byte[] result = cache.get(input);
        if (result == null) {
            synchronized (lock) {
                result = cache.get(input);
                if (result == null) {
                    result = doDigest(input);
                    cache.put(input, result);
                }
            }
        }
        return result;
    }
 
    protected abstract byte[] doDigest(byte[] input);
}