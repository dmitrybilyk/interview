package com.conduct.interview._3_spring.prototype_in_singleton;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public class SingletonBeanFixed {

    public String getPrototypeId() {
        return getPrototypeInstance().getId();
    }

    @Lookup
    public PrototypeBean getPrototypeInstance() {
        // Spring overrides this method to return a NEW prototype instance
        return null; 
    }
}