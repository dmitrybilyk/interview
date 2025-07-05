package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.future;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Task 1: Fetch account from database
        Future<Account> accountFuture = executor.submit(() -> {
            Thread.sleep(1000); // Simulate database query
            return new Account(1L, new BigDecimal("1000.00"));
        });

        // Task 2: Validate transaction via API
        Future<ValidationResult> validationFuture = executor.submit(() -> {
            Thread.sleep(800); // Simulate API call
            return new ValidationResult(true);
        });

        // Block and wait for results
        Account account = accountFuture.get(); // Blocks until complete
        ValidationResult validation = validationFuture.get(); // Blocks until complete

        // Combine results
        if (validation.isValid()) {
            System.out.println("Account balance: " + account.getBalance());
        } else {
            System.out.println("Transaction validation failed");
        }

        executor.shutdown();
    }
}

class Account {
    private Long id;
    private BigDecimal balance;

    public Account(Long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }
    public BigDecimal getBalance() { return balance; }
}

class ValidationResult {
    private boolean isValid;

    public ValidationResult(boolean isValid) {
        this.isValid = isValid;
    }
    public boolean isValid() { return isValid; }
}