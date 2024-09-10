package com.conduct.interview._3_spring.bean_lifecycle.phases._5Application_Context_Aware_Interface;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class ApplicationContextAware implements org.springframework.context.ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
