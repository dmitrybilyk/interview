package com.conduct.interview._3_spring._2_bean_lifecycle;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanLifecycleDemo {

    public static void main(String[] args) {
        System.out.println("== creating context: watch steps 1-9 happen for someBean/anotherSomeBean ==");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "com/conduct/interview/_3_spring/_2_bean_lifecycle/bean-lifecycle-context.xml");

        System.out.println("== context ready, using the managed resource ==");
        context.getBean(ManagedResource.class).doWork();

        System.out.println("== closing context: watch steps 10-12 happen, then the resource close ==");
        // destroy callbacks (@PreDestroy, DisposableBean, custom destroy-method) only run
        // when the context is explicitly closed (or a JVM shutdown hook is registered via
        // context.registerShutdownHook()) - forgetting this is a classic reason "my
        // @PreDestroy never fires" in a plain main() method.
        context.close();
    }
}
