package com.conduct.interview._3_spring._6_bean_post_processors_and_factory_post_processors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/** Works on metadata only - runs before a single bean is instantiated. */
public class BeanDefinitionCountingPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] names = beanFactory.getBeanDefinitionNames();
        System.out.println("BeanFactoryPostProcessor: " + names.length + " bean definitions registered so far:");
        for (String name : names) {
            System.out.println("  - " + name + " (not necessarily instantiated yet)");
        }
    }
}
