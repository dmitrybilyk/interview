package com.conduct.interview.practise.spring.transactions.isolation_levels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ReadBalanceService {

    private final AccountRepository accountRepository;

    public ReadBalanceService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

//    @Transactional(isolation = Isolation.SERIALIZABLE)
//    @Transactional(isolation = Isolation.REPEATABLE_READ)
//    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public int getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();

        log.info("ReadBalanceService: Reading balance: {}", account.getBalance());

        // Simulate a delay during reading
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("ReadBalanceService: After delay of 2s, balance read is: {}", account.getBalance());

        return account.getBalance();
    }
}
