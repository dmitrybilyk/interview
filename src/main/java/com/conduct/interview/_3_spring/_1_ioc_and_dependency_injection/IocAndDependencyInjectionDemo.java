package com.conduct.interview._3_spring._1_ioc_and_dependency_injection;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IocAndDependencyInjectionDemo {

    public static void main(String[] args) {
        System.out.println("-- creating context (watch beans get built before we ask for any) --");
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "com/conduct/interview/_3_spring/_1_ioc_and_dependency_injection/ioc-context.xml")) {

            System.out.println("-- context ready, now fetching beans --");

            // ApplicationContext IS-A BeanFactory - it just adds messaging,
            // events, environment abstraction etc. on top.
            BeanFactory beanFactory = context;
            Car car = beanFactory.getBean("car", Car.class);

            System.out.println(car.drive());
        }
    }
}
