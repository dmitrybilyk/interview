package com.conduct.interview._3_spring._4_aop;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AopDemo {

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "com/conduct/interview/_3_spring/_4_aop/aop-context.xml")) {

            GreetingService greetingService = context.getBean(GreetingService.class);
            System.out.println("calling greetingService.greet(...)");
            System.out.println("result: " + greetingService.greet("world"));
            System.out.println("proxy class : " + greetingService.getClass().getName());
            System.out.println("is JDK proxy: " + AopUtils.isJdkDynamicProxy(greetingService));

            System.out.println();

            PlainCalculator calculator = context.getBean(PlainCalculator.class);
            System.out.println("calling calculator.add(...)");
            System.out.println("result: " + calculator.add(2, 3));
            System.out.println("proxy class  : " + calculator.getClass().getName());
            System.out.println("is CGLIB proxy: " + AopUtils.isCglibProxy(calculator));
        }
    }
}
