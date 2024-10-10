package com.conduct.interview.practise.spring.lifecycle;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoodController {
    @PostConstruct
    private void postConstruct() {
        System.out.println("abcd");
    }

    @Autowired
    private GoodBean goodBean;
    @GetMapping("/result")
    public String goodResult() {
        return "good result";
    }
}
