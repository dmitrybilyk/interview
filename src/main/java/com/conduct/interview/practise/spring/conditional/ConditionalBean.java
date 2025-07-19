package com.conduct.interview.practise.spring.conditional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty("enable.conditional")
public class ConditionalBean {

    public void sayHello() {
        System.out.println("Hello World!");
    }

}
