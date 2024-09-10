package com.conduct.interview._3_spring.bean_lifecycle;

import org.springframework.context.annotation.Bean;

//@Configuration
public class BeanLifeCycleConfiguration {
    @Bean(initMethod = "customInitMethod", destroyMethod = "customDestroyMethod")
    public SomeBean someBean() {
        return new SomeBean("Some String");
    }

    @Bean
    public AnotherSomeBean anotherSomeBean() {
        return new AnotherSomeBean();
    }
}
