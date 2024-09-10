package com.conduct.interview._3_spring.bean_lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class SomeBean implements BeanNameAware, BeanFactoryAware, ApplicationContextAware,
        BeanPostProcessor, InitializingBean, DisposableBean {
    public String getName() {
        return name;
    }

    private String name;
    private AnotherSomeBean anotherSomeBean;

    public SomeBean() {
        System.out.println("1 - Constructor");
    }

    public SomeBean(String name) {
        System.out.println("1 - Constructor Really used with Name");
        this.name = name;
    }

    public void setAnotherSomeBean(AnotherSomeBean anotherSomeBean) {
        System.out.println("2 - Setter");
        this.anotherSomeBean = anotherSomeBean;
    }

    @Override
    public void setBeanName(String s) {
        System.out.println("3 - Bean name " +  s);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("4 - Bean Factory Aware");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("5 - Application Context Aware");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("6 - Bean Post Processor - before initialization");
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("6 - Bean Post Processor - after initialization");
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @PostConstruct
    public void initMethod() {
        System.out.println("7 - Post Construct Annotation");
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("8 - Initializing Bean - afterPropertiesSet");
    }

    @PostConstruct
    public void customInitMethod() {
        System.out.println("9 - Custom Init Method");
    }
    @PreDestroy
    public void destroyMethod() {
        System.out.println("10 - PreDestroy Annotation");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("11 - DisposableBean");
    }

    @PreDestroy
    public void customDestroyMethod() {
        System.out.println("12 - Custom Destroy Method");
    }

    public void setName(String name) {
        this.name = name;
    }
}
