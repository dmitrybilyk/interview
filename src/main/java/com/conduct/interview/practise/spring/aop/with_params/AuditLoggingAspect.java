package com.conduct.interview.practise.spring.aop.with_params;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLoggingAspect {

    @Before("execution(* com.conduct.interview.practise.spring.aop.with_params.UserService.performAction(..))")
    public void logMethodArguments(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length >= 2) {
            Long userId = (Long) args[0];
            String action = (String) args[1];

            System.out.println("AOP LOG: userId = " + userId + ", action = " + action);
        }
    }
}
