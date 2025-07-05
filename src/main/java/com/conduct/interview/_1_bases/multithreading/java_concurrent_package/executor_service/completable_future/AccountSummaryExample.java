package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.completable_future;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountSummaryExample {
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);

    public static void main(String[] args) {
        try {
            // Fetch account summary for accountId 1
            CompletableFuture<AccountSummary> summaryFuture = getAccountSummary(1L);
            AccountSummary summary = summaryFuture.join(); // Block for demo purposes
            System.out.println(summary);
        } finally {
            executor.shutdown();
        }
    }

    public static CompletableFuture<AccountSummary> getAccountSummary(Long accountId) {
        // Task 1: Simulate fetching account details
        CompletableFuture<Account> accountFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000); // Simulate database query
                if (accountId < 0) throw new IllegalArgumentException("Invalid account ID");
                return new Account(accountId, new BigDecimal("1000.00"));
            } catch (Exception e) {
                System.err.println("Account fetch failed: " + e.getMessage());
                return null; // Partial result
            }
        }, executor);

        // Task 2: Simulate validating account via external API
        CompletableFuture<ValidationResult> validationFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(800); // Simulate API call
                if (accountId == 999L) throw new RuntimeException("API validation error");
                return new ValidationResult(true, null);
            } catch (Exception e) {
                System.err.println("Validation failed: " + e.getMessage());
                return new ValidationResult(false, e.getMessage());
            }
        }, executor);

        // Task 3: Simulate fetching recent transactions
        CompletableFuture<List<Transaction>> transactionsFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1200); // Simulate database query
                return Arrays.asList(
                        new Transaction("tx1", accountId, new BigDecimal("100.00"), LocalDateTime.now()),
                        new Transaction("tx2", accountId, new BigDecimal("200.00"), LocalDateTime.now().minusHours(1)),
                        new Transaction("tx3", accountId, new BigDecimal("150.00"), LocalDateTime.now().minusHours(2))
                );
            } catch (Exception e) {
                System.err.println("Transaction fetch failed: " + e.getMessage());
                return List.of(); // Empty list on failure
            }
        }, executor);

        // Combine results
        return CompletableFuture.allOf(accountFuture, validationFuture, transactionsFuture)
                .thenApply(ignored -> {
                    Account account = accountFuture.join();
                    ValidationResult validation = validationFuture.join();
                    List<Transaction> transactions = transactionsFuture.join();

                    AccountSummary summary;
                    if (account != null) {
                        summary = new AccountSummary(accountId, account.balance,
                                validation.isValid ? "VALID" : "INVALID: " + validation.errorMessage,
                                null, transactions);
                    } else {
                        summary = new AccountSummary(accountId, null,
                                validation.isValid ? "VALID" : "INVALID: " + validation.errorMessage,
                                "Account details unavailable", transactions);
                    }

                    return summary;
                })
                .exceptionally(throwable -> {
                    System.err.println("Error aggregating summary: " + throwable.getMessage());
                    return new AccountSummary(accountId, null, "ERROR: " + throwable.getMessage(),
                            throwable.getMessage(), List.of());
                });
    }
}

class Account {
    Long accountId;
    BigDecimal balance;

    Account(Long accountId, BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }
}

class ValidationResult {
    boolean isValid;
    String errorMessage;

    ValidationResult(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }
}

class Transaction {
    String id;
    Long accountId;
    BigDecimal amount;
    LocalDateTime createdAt;

    Transaction(String id, Long accountId, BigDecimal amount, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.createdAt = createdAt;
    }
}

class AccountSummary {
    Long accountId;
    BigDecimal balance;
    String validationStatus;
    String errorMessage;
    List<Transaction> recentTransactions;

    // Constructor for all fields
    public AccountSummary(Long accountId, BigDecimal balance, String validationStatus,
                          String errorMessage, List<Transaction> recentTransactions) {
        this.accountId = accountId;
        this.balance = balance;
        this.validationStatus = validationStatus;
        this.errorMessage = errorMessage;
        this.recentTransactions = recentTransactions;
    }

    // No-arg constructor for flexibility
    public AccountSummary() {
        this.accountId = null;
        this.balance = null;
        this.validationStatus = null;
        this.errorMessage = null;
        this.recentTransactions = null;
    }

    @Override
    public String toString() {
        return "AccountSummary{" +
                "accountId=" + accountId +
                ", balance=" + balance +
                ", validationStatus='" + validationStatus + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", recentTransactions=" + recentTransactions +
                '}';
    }
}