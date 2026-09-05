package com.conduct.interview.spring;

import com.conduct.interview._3_spring._3_bean_scopes.SingletonBeanNotFixed;
import com.conduct.interview._3_spring._3_bean_scopes.SingletonBeanWithObjectFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

// see com.conduct.interview._3_spring._3_bean_scopes for the full writeup and more fixes
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:com/conduct/interview/_3_spring/_3_bean_scopes/bean-scopes-context.xml")
public class ScopeTest {

    @Autowired
    private SingletonBeanNotFixed singletonBeanNotFixed;

    @Autowired
    private SingletonBeanWithObjectFactory singletonBeanWithObjectFactory;

    @Test
    void prototypeInjectedDirectlyIntoASingletonIsFrozenAtWiringTime() {
        String id1 = singletonBeanNotFixed.getPrototypeId();
        String id2 = singletonBeanNotFixed.getPrototypeId();

        assertEquals(id1, id2, "a plain reference to a prototype bean is only ever resolved once");
    }

    @Test
    void objectFactoryFixesItByReResolvingOnEveryCall() {
        String id1 = singletonBeanWithObjectFactory.getPrototypeId();
        String id2 = singletonBeanWithObjectFactory.getPrototypeId();

        assertNotEquals(id1, id2, "ObjectFactory.getObject() should fetch a fresh prototype every call");
    }
}
