package com.conduct.interview.practise.spring.aop.with_params;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public void performAction(Long userId, String action) {
        System.out.println("Performing action: " + action + " for user " + userId);
    }
}
