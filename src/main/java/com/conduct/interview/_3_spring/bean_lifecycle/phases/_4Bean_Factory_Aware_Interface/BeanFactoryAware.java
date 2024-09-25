package com.conduct.interview._3_spring.bean_lifecycle.phases._4Bean_Factory_Aware_Interface;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

public class BeanFactoryAware implements org.springframework.beans.factory.BeanFactoryAware {

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {}
}
