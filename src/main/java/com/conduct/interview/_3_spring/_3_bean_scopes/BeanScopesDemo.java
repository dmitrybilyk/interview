package com.conduct.interview._3_spring._3_bean_scopes;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanScopesDemo {

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "com/conduct/interview/_3_spring/_3_bean_scopes/bean-scopes-context.xml")) {

            SingletonBeanNotFixed broken = context.getBean(SingletonBeanNotFixed.class);
            System.out.println("BUG      -> " + broken.getPrototypeId() + " == " + broken.getPrototypeId()
                    + " (same id both times, frozen at wiring time)");

            SingletonBeanWithObjectFactory withFactory =
                    context.getBean(SingletonBeanWithObjectFactory.class);
            System.out.println("FIX 1    -> " + withFactory.getPrototypeId() + " != " + withFactory.getPrototypeId()
                    + " (ObjectFactory.getObject() re-resolves every call)");

            SingletonBeanWithLookup withLookup = context.getBean(SingletonBeanWithLookup.class);
            System.out.println("FIX 2    -> " + withLookup.getPrototypeId() + " != " + withLookup.getPrototypeId()
                    + " (@Lookup, actual class: " + withLookup.getClass().getSimpleName() + ")");

            SingletonBeanWithScopedProxy withProxy =
                    context.getBean(SingletonBeanWithScopedProxy.class);
            System.out.println("FIX 3    -> " + withProxy.getPrototypeId() + " != " + withProxy.getPrototypeId()
                    + " (scoped-proxy, singleton's field type: " + withProxy.getClass().getSimpleName() + ")");
        }
    }
}
