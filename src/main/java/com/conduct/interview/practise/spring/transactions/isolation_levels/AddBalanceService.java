package com.conduct.interview.practise.spring.transactions.isolation_levels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AddBalanceService {

    private final AccountRepository accountRepository;

    public AddBalanceService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void updateBalance(Long accountId, int amount) {
        Account account = accountRepository.findById(accountId).orElseThrow();

        log.info("AddBalanceService: Reading initial balance: {}", account.getBalance());

        account.setBalance(account.getBalance() + amount);

        log.info("AddBalanceService: Updated balance to: {}", account.getBalance());

        // Simulate a delay before committing
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        accountRepository.save(account);
        log.info("AddBalanceService: Transaction committed with balance: {}", account.getBalance());
    }
}
