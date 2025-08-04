package com.conduct.interview.auto;

public interface SecretStorageDao<K, V> {
    void store(K key, V secret);

    V retrieve(K key);

    boolean keyExists(K key);
}
