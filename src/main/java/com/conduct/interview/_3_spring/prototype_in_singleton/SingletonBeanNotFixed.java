package com.conduct.interview._3_spring.prototype_in_singleton;

import org.springframework.stereotype.Component;

@Component
public class SingletonBeanNotFixed {

    private final PrototypeBean prototypeFactory;

    public SingletonBeanNotFixed(PrototypeBean prototypeFactory) {
        this.prototypeFactory = prototypeFactory;
    }

    public String getPrototypeId() {
        return prototypeFactory.getId();
    }
}
