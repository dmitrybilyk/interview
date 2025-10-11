package com.conduct.interview.practise.spring.aop.with_params;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    public void performAction(Long userId, String action) {
        log.info("In User Service thread name is - " + Thread.currentThread().getName());
        System.out.println("Performing action: " + action + " for user " + userId);
    }
}
