package com.conduct.interview._3_spring._3_bean_scopes;

/**
 * Fix #3: scoped-proxy. This class doesn't know or care that
 * prototypeBean is scoped - it just holds a normal-looking reference.
 * The XML config swaps in a proxy that re-resolves the scope on every
 * method call, entirely transparently.
 */
public class SingletonBeanWithScopedProxy {

    private final PrototypeBean prototype;

    public SingletonBeanWithScopedProxy(PrototypeBean prototype) {
        this.prototype = prototype;
    }

    public String getPrototypeId() {
        return prototype.getId();
    }
}
