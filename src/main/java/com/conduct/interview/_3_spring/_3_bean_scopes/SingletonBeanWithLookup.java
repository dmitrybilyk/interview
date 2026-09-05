package com.conduct.interview._3_spring._3_bean_scopes;

import org.springframework.beans.factory.annotation.Lookup;

/**
 * Fix #2: @Lookup. getPrototypeInstance() has no body - it's abstract, so
 * this class can't normally be instantiated. Spring generates a concrete
 * CGLIB subclass at runtime that implements the missing method for you,
 * fetching a fresh "prototypeBean" from the container on every call.
 */
public abstract class SingletonBeanWithLookup {

    public String getPrototypeId() {
        return getPrototypeInstance().getId();
    }

    @Lookup("prototypeBean")
    protected abstract PrototypeBean getPrototypeInstance();
}
