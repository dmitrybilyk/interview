package com.conduct.interview.practise.spring.aop;

import com.conduct.interview.practise.spring.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AopController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/aop")
    public String getResource() {
        orderService.placeOrder("my item");
        return "aop checked";
    }
}
