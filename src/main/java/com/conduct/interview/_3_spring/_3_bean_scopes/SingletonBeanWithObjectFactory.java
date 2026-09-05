package com.conduct.interview._3_spring._3_bean_scopes;

import org.springframework.beans.factory.ObjectFactory;

/**
 * Fix #1: inject a factory, ask it for a fresh instance on every call.
 *
 * ObjectFactory<T> is just a one-method interface: T getObject(). The
 * instance injected here is created by ObjectFactoryCreatingFactoryBean
 * (see bean-scopes-context.xml) to do exactly one thing:
 * getObject() -> applicationContext.getBean("prototypeBeanPlain").
 * Because that target bean is scope="prototype", each call returns a
 * brand-new PrototypeBean - unlike injecting a PrototypeBean directly,
 * which resolves it once and freezes it forever (see SingletonBeanNotFixed).
 */
public class SingletonBeanWithObjectFactory {

    private final ObjectFactory<PrototypeBean> prototypeFactory;

    public SingletonBeanWithObjectFactory(ObjectFactory<PrototypeBean> prototypeFactory) {
        this.prototypeFactory = prototypeFactory;
    }

    public String getPrototypeId() {
        return prototypeFactory.getObject().getId(); // a fresh PrototypeBean every call
    }
}
