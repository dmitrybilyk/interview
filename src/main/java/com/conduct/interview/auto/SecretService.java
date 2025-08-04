package com.conduct.interview.auto;

//1. Let's keep it simple
//Storage
//Write a method that takes two parameters: a key and a secret.
//Store them and return the given key.
//- key:      f.e. "mY-cUsTom-kEy"
//        - secret:   f.e. "My secret no one should know!"
//Constraint: key length <= 20

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

public class SecretService<K, V> {
    private final SecretStorageDao<K, V> secretStorageDao;

    public SecretService(SecretStorageDao<K, V> secretStorageDao) {
        this.secretStorageDao = secretStorageDao;
    }

    public K store(K key, V secret) {
//        validateKey(key, secretStorageDao);
        validateSecret(secret);
        secretStorageDao.store(key, secret);
        return key;
    }

//    public K store(K key, V secret) {
////        validateKey(key, secretStorageDao);
//        validateSecret(secret);
//        secretStorageDao.store(key, secret);
//        return key;
//    }

    public V retrieve(K key) {
//        validateKeyIsBlank(key);
        if (!secretStorageDao.keyExists(key)) {
            throw new IllegalArgumentException("Secret key does not exist");
        }
        return secretStorageDao.retrieve(key);
    }

//    public K store(V secret) {
//        String key = UUID.randomUUID().toString().substring(0, 10);
//        secretStorageDao.store(key, secret);
//        return key;
//    }

    private void validateSecret(V secret) {
        if (secret == null) {
            throw new IllegalArgumentException("Can't store the secret. Secret is not valid");
        }
    }
//
//    private static void validateKey(K key, SecretStorageDao secretStorageDao) {
//        validateKeyIsBlank(key);
//        if (key.length() > 20) {
//            throw new IllegalArgumentException("Can't store the secret. Secret length exceeds 20");
//        }
//        if (secretStorageDao.keyExists(key)) {
//            throw new IllegalArgumentException("Secret key already exists");
//        }
//    }
//
//    private static void validateKeyIsBlank(K key) {
//        if (StringUtils.isBlank(key)) {
//            throw new IllegalArgumentException("Can't store the secret. Key is null");
//        }
//    }

}
