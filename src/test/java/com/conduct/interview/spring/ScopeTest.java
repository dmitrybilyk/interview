package com.conduct.interview.spring;

import com.conduct.interview._3_spring.prototype_in_singleton.PrototypeBean;
import com.conduct.interview._3_spring.prototype_in_singleton.SingletonBean;
import com.conduct.interview._3_spring.prototype_in_singleton.SingletonBeanNotFixed;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PrototypeBean.class, SingletonBean.class})
//@ContextConfiguration(classes = {PrototypeBean.class, SingletonBeanNotFixed.class})
public class ScopeTest {

    @Autowired
//    private SingletonBeanNotFixed singletonBean;
    private SingletonBean singletonBean;

    @Test
    void testPrototypeInjectedIntoSingleton() {
        String id1 = singletonBean.getPrototypeId();
        String id2 = singletonBean.getPrototypeId();

        System.out.println("Prototype ID 1: " + id1);
        System.out.println("Prototype ID 2: " + id2);

        assertNotEquals(id1, id2, "Prototype instances should be different");
    }

//    1. Let's keep it simple
//    Storage
//    Write a method that takes two parameters: a key and a secret.
//    Store them and return the given key.
//- key:      f.e. "mY-cUsTom-kEy"
//            - secret:   f.e. "My secret no one should know!"
//    Constraint: key length <= 20
}
