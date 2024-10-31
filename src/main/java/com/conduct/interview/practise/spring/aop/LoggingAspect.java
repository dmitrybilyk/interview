package com.conduct.interview.practise.spring.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // Pointcut expression to match all methods in OrderService
    @Before("execution(* com.conduct.interview.practise.spring.aop.OrderService.*(..))")
    public void logBeforeMethod() {
        System.out.println("LoggingAspect: Method is about to execute...");
    }
}
