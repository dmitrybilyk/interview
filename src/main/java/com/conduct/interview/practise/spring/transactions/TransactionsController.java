package com.conduct.interview.practise.spring.transactions;

import com.conduct.interview.practise.spring.transactions.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("postgres")
public class TransactionsController {

    @Autowired
    private MyService myService;

    @GetMapping("/transaction/check")
    public String testTransaction() {
        try {
            myService.outerTransaction();
        } catch (Exception e) {
            return "Transaction failed: " + e.getMessage();
        }
        return "Transaction completed";
    }
}
