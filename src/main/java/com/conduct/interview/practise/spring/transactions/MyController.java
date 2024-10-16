package com.conduct.interview.practise.spring.transactions;

import com.conduct.interview.practise.spring.transactions.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @Autowired
    private MyService myService;

    @GetMapping("/transaction/requires-new")
    public String testTransaction() {
        try {
            myService.outerTransaction();
        } catch (Exception e) {
            return "Transaction failed: " + e.getMessage();
        }
        return "Transaction completed";
    }
}