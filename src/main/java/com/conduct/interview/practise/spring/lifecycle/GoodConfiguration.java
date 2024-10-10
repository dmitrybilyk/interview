package com.conduct.interview.practise.spring.lifecycle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class GoodConfiguration {
    @Bean
    @Primary
    public GoodBean getGoodBean() {
        return new GoodBean("better name", 33);
    }
}
