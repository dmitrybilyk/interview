package com.conduct.interview.auto;

//1. Let's keep it simple
//Storage
//Write a method that takes two parameters: a key and a secret.
//Store them and return the given key.
//- key:      f.e. "mY-cUsTom-kEy"
//        - secret:   f.e. "My secret no one should know!"
//Constraint: key length <= 20

import io.micrometer.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

public class SecretService {
    private final SecretStorageDao secretStorageDao;

    public SecretService(SecretStorageDao secretStorageDao) {
        this.secretStorageDao = secretStorageDao;
    }

    public String store(String key, String secret) {
        validateKey(key, secretStorageDao);
        validateSecret(secret);
        secretStorageDao.store(key, secret);
        return key;
    }

    public Integer store(Integer key, String secret) {
//        validateKey(key, secretStorageDao);
        validateSecret(secret);
        secretStorageDao.store(key, secret);
        return key;
    }

    public String retrieve(String key) {
        validateKeyIsBlank(key);
        if (!secretStorageDao.keyExists(key)) {
            throw new IllegalArgumentException("Secret key does not exist");
        }
        return secretStorageDao.retrieve(key);
    }

    public String store(String secret) {
        String key = UUID.randomUUID().toString().substring(0, 10);
        secretStorageDao.store(key, secret);
        return key;
    }

    private static void validateSecret(String secret) {
        if (StringUtils.isBlank(secret)) {
            throw new IllegalArgumentException("Can't store the secret. Secret is not valid");
        }
    }

    private static void validateKey(String key, SecretStorageDao secretStorageDao) {
        validateKeyIsBlank(key);
        if (key.length() > 20) {
            throw new IllegalArgumentException("Can't store the secret. Secret length exceeds 20");
        }
        if (secretStorageDao.keyExists(key)) {
            throw new IllegalArgumentException("Secret key already exists");
        }
    }

    private static void validateKeyIsBlank(String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Can't store the secret. Key is null");
        }
    }

}
