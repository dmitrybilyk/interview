package com.conduct.interview.auto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecretStorageStringMapDaoImpl implements SecretStorageDao {
    private final Map<String, String> storage = new ConcurrentHashMap();

    @Override
    public void store(String key, String secret) {
        this.storage.put(key, secret);
    }

    @Override
    public String retrieve(String key) {
        return this.storage.get(key);
    }

    @Override
    public boolean keyExists(String key) {
        return this.storage.containsKey(key);
    }
}
