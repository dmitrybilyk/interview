package com.conduct.interview._3_spring._3_bean_scopes;

/** The bug: a plain reference to a prototype bean is only ever resolved once. */
public class SingletonBeanNotFixed {

    private final PrototypeBean prototype;

    public SingletonBeanNotFixed(PrototypeBean prototype) {
        this.prototype = prototype;
    }

    public String getPrototypeId() {
        return prototype.getId();
    }
}
