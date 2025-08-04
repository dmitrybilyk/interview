package com.conduct.interview.auto;

public interface SecretStorageDao {
    void store(String key, String secret);

    String retrieve(String key);

    boolean keyExists(String key);
}
