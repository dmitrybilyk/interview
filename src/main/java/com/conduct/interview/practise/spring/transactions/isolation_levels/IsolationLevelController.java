package com.conduct.interview.practise.spring.transactions.isolation_levels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@Slf4j
@Profile("postgres")
public class IsolationLevelController {

    private final ReadBalanceService readBalanceService;
    private final AddBalanceService addBalanceService;

    public IsolationLevelController(ReadBalanceService readBalanceService, AddBalanceService addBalanceService) {
        this.readBalanceService = readBalanceService;
        this.addBalanceService = addBalanceService;
    }

    @GetMapping("/transactions/isolation")
    public String testIsolationLevels(@RequestParam Long accountId, @RequestParam int amount) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Start reading and updating transactions concurrently
        Future<?> readFuture = executorService.submit(() -> {
            log.info("Starting AccountService (read transaction)...");
            int balance = readBalanceService.getBalance(accountId);
            log.info("Final balance read by AccountService: {}", balance);
        });

        Future<?> updateFuture = executorService.submit(() -> {
            log.info("Starting BalanceService (update transaction)...");
            addBalanceService.updateBalance(accountId, amount);
            log.info("BalanceService update completed.");
        });

        executorService.shutdown();

        try {
            readFuture.get();
            updateFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Transactions executed. Check the console output for isolation level effects.";
    }
}


//INSERT INTO my_entity (id, name) VALUES
//                                 (1, 'Initial Entity 1'),
//                                         (2, 'Initial Entity 2');
//
//INSERT INTO account (owner, balance) VALUES
//                                         ('John Doe', 100),
//                                                 ('Jane Smith', 200);