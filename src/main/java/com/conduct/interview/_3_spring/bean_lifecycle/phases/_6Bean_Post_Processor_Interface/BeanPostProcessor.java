package com.conduct.interview._3_spring.bean_lifecycle.phases._6Bean_Post_Processor_Interface;

import org.springframework.beans.BeansException;

public class BeanPostProcessor
    implements org.springframework.beans.factory.config.BeanPostProcessor {
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return org.springframework.beans.factory.config.BeanPostProcessor.super
        .postProcessBeforeInitialization(bean, beanName);
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return org.springframework.beans.factory.config.BeanPostProcessor.super
        .postProcessAfterInitialization(bean, beanName);
  }
}
