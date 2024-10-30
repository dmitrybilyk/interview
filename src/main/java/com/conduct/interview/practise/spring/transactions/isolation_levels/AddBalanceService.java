package com.conduct.interview.practise.spring.transactions.isolation_levels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AddBalanceService {

    private final JdbcTemplate jdbcTemplate;

    public AddBalanceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateBalance(Long accountId, int amount) {
        log.info("Starting transaction to update balance.");

        // Read initial balance directly from the database
        int currentBalance = jdbcTemplate.queryForObject(
                "SELECT balance FROM account WHERE id = ?",
                Integer.class, accountId
        );

        log.info("Initial balance read: {}", currentBalance);

        // Update balance in the database
        int updatedBalance = currentBalance + amount;
        jdbcTemplate.update("UPDATE account SET balance = ? WHERE id = ?", updatedBalance, accountId);

        log.info("Updated balance to: {}", updatedBalance);

        // Simulate delay before committing
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted", e);
        }

        log.info("Transaction committed with balance: {}", updatedBalance);
    }
}