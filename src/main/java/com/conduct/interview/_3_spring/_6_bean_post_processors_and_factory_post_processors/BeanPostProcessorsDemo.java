package com.conduct.interview._3_spring._6_bean_post_processors_and_factory_post_processors;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanPostProcessorsDemo {

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "com/conduct/interview/_3_spring/_6_bean_post_processors_and_factory_post_processors/post-processors-context.xml")) {

            Greeter greeter = context.getBean(Greeter.class);
            System.out.println("greeter.greet() -> " + greeter.greet());
        }
    }
}
