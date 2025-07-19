package com.conduct.interview.practise.check;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SomeConfiguration {
    @Bean(name = "myName")
//    @Primary
    public SomeClass someClass3() {
        return new SomeClass();
    }

    @Bean(name = "myName2")
    public SomeClass someClass2() {
        return new SomeClass();
    }
}
