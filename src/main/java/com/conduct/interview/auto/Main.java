package com.conduct.interview.auto;

public class Main {
    public static void main(String[] args) {
        SecretService<Integer, String> secretService = new SecretService<>(new SecretStorageMapDaoImpl<>());
        secretService.store(2, "fdfdf");
    }
}
