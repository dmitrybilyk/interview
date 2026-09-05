package com.conduct.interview._3_spring._12_spring_data_jpa;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Proxy;
import java.util.List;

public class SpringDataJpaDemo {

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "com/conduct/interview/_3_spring/_12_spring_data_jpa/spring-data-jpa-context.xml")) {

            ProductRepository repository = context.getBean(ProductRepository.class);

            System.out.println("repository interface  : " + ProductRepository.class.getName());
            System.out.println("repository actual class: " + repository.getClass().getName());
            System.out.println("is a JDK dynamic proxy : " + Proxy.isProxyClass(repository.getClass()));

            repository.save(new Product("USB-C cable"));
            repository.save(new Product("USB-C hub"));
            repository.save(new Product("HDMI cable"));

            List<Product> usbProducts = repository.findByNameContaining("USB-C");
            System.out.println();
            System.out.println("findByNameContaining(\"USB-C\") -> " + usbProducts);
            System.out.println("count() -> " + repository.count());
        }
    }
}
