package com.conduct.interview._3_spring._6_bean_post_processors_and_factory_post_processors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/** Works on real bean instances, once per bean, around initialization. */
public class BeanCreationTimingPostProcessor implements BeanPostProcessor {

    private final ThreadLocal<Long> startedAt = new ThreadLocal<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        startedAt.set(System.nanoTime());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        long micros = (System.nanoTime() - startedAt.get()) / 1_000;
        System.out.println("BeanPostProcessor: '" + beanName + "' (" + bean.getClass().getSimpleName()
                + ") initialized in " + micros + " microseconds");
        return bean;
    }
}
