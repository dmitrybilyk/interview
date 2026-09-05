package com.conduct.interview._3_spring._2_bean_lifecycle.phases._11Disposable_Bean_Interface;

public class DisposableBean implements org.springframework.beans.factory.DisposableBean {
  @Override
  public void destroy() throws Exception {}
}
