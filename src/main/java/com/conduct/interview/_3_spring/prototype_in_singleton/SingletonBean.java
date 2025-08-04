package com.conduct.interview._3_spring.prototype_in_singleton;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
public class SingletonBean {

    private final ObjectFactory<PrototypeBean> prototypeFactory;

    public SingletonBean(ObjectFactory<PrototypeBean> prototypeFactory) {
        this.prototypeFactory = prototypeFactory;
    }

    public String getPrototypeId() {
        return prototypeFactory.getObject().getId();
    }
}
