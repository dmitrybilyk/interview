package com.conduct.interview._3_spring._5_transactions;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public class TransferService {

    private static final long SUSPICIOUSLY_LARGE = 1000L;

    private final JdbcTemplate jdbcTemplate;

    public TransferService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void transfer(String fromAccount, String toAccount, long amount) {
        debit(fromAccount, amount);
        if (amount >= SUSPICIOUSLY_LARGE) {
            throw new IllegalArgumentException(
                    "transfer of " + amount + " looks fraudulent - aborting (this should roll back the debit above)");
        }
        credit(toAccount, amount);
    }

    /** Same two statements, no @Transactional - a failure here leaves a real, persisted inconsistency. */
    public void transferWithoutTransaction(String fromAccount, String toAccount, long amount) {
        debit(fromAccount, amount);
        if (amount >= SUSPICIOUSLY_LARGE) {
            throw new IllegalArgumentException(
                    "transfer of " + amount + " looks fraudulent - aborting (nothing will undo the debit above)");
        }
        credit(toAccount, amount);
    }

    public long balanceOf(String account) {
        Long balance = jdbcTemplate.queryForObject(
                "select balance from accounts where name = ?", Long.class, account);
        return balance == null ? 0 : balance;
    }

    private void debit(String account, long amount) {
        jdbcTemplate.update("update accounts set balance = balance - ? where name = ?", amount, account);
    }

    private void credit(String account, long amount) {
        jdbcTemplate.update("update accounts set balance = balance + ? where name = ?", amount, account);
    }
}
