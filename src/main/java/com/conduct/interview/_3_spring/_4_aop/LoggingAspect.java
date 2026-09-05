package com.conduct.interview._3_spring._4_aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class LoggingAspect {

    @Before("execution(* com.conduct.interview._3_spring._4_aop.*.*(..))")
    public void logBefore(org.aspectj.lang.JoinPoint joinPoint) {
        System.out.println("  [Before]  " + joinPoint.getSignature().toShortString()
                + " args=" + java.util.Arrays.toString(joinPoint.getArgs()));
    }

    @Around("execution(* com.conduct.interview._3_spring._4_aop.PlainCalculator.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("  [Around]  before proceed()");
        Object result = joinPoint.proceed(); // the only advice type that can skip/alter the call
        System.out.println("  [Around]  after proceed(), result=" + result);
        return result;
    }
}
