package com.conduct.interview._3_spring._11_spring_boot_autoconfiguration;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ClassUtils;

/**
 * @ConditionalOnClass, with the annotation peeled away: a plain classpath
 * check deciding whether to register a bean definition at all.
 */
public class ManualAutoConfigurationDemo {

    public static void main(String[] args) {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            conditionallyRegister(context,
                    "com.fasterxml.jackson.databind.ObjectMapper",
                    "jacksonAvailableBean",
                    JacksonAvailableBean.class);

            conditionallyRegister(context,
                    "com.some.totally.made.up.library.Client",
                    "someMissingLibraryBean",
                    SomeMissingLibraryBean.class);

            context.refresh();

            System.out.println();
            System.out.println("beans actually registered: " + java.util.Arrays.toString(context.getBeanDefinitionNames()));
        }
    }

    /** This method body is, mechanically, what @ConditionalOnClass compiles down to. */
    private static void conditionallyRegister(
            GenericApplicationContext context, String requiredClassName, String beanName, Class<?> beanClass) {
        boolean present = ClassUtils.isPresent(requiredClassName, ManualAutoConfigurationDemo.class.getClassLoader());
        System.out.println((present ? "MATCH   " : "NO MATCH") + " - " + requiredClassName
                + " present=" + present + " -> " + (present ? "registering " + beanName : "skipping " + beanName));
        if (present) {
            context.registerBean(beanName, beanClass);
        }
    }
}
