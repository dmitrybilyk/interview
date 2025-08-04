package com.conduct.interview.auto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecretStorageIntegerMapDaoImpl<K, V> implements SecretStorageDao<K, V> {
    private final Map<K, V> storage = new ConcurrentHashMap<>();

    @Override
    public void store(K key, V secret) {
        this.storage.put(key, secret);
    }

    @Override
    public V retrieve(K key) {
        return this.storage.get(key);
    }

    @Override
    public boolean keyExists(K key) {
        return this.storage.containsKey(key);
    }
}
