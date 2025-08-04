package com.conduct.interview.auto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecretStorageIntegerMapDaoImpl implements SecretStorageDao {
    private final Map<Integer, String> storage = new ConcurrentHashMap<>();

    @Override
    public void store(Integer key, String secret) {
        this.storage.put(key, secret);
    }

    @Override
    public String retrieve(Integer key) {
        return this.storage.get(key);
    }

    @Override
    public boolean keyExists(Integer key) {
        return this.storage.containsKey(key);
    }
}
