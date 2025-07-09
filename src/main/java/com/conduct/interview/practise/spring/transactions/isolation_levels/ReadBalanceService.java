package com.conduct.interview.practise.spring.transactions.isolation_levels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Profile("postgres")
public class ReadBalanceService {

    private final JdbcTemplate jdbcTemplate;

    public ReadBalanceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
//    Here it's just seeing initial data, if concurrently another transaction is running it
//    will ignore
//    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int getBalance(Long accountId) {
        int balance = jdbcTemplate.queryForObject(
                "SELECT balance FROM account WHERE id = ?",
                Integer.class, accountId
        );

        log.info("ReadBalanceService: Reading balance: {}", balance);

        // Simulate a delay during reading
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        balance = jdbcTemplate.queryForObject(
                "SELECT balance FROM account WHERE id = ?",
                Integer.class, accountId
        );

        log.info("ReadBalanceService: After delay of 2s, balance read is: {}", balance);

        // Simulate a delay during reading
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        balance = jdbcTemplate.queryForObject(
                "SELECT balance FROM account WHERE id = ?",
                Integer.class, accountId
        );

        log.info("ReadBalanceService: After delay of 2s, balance read is: {}", balance);


        return balance;
    }
}
