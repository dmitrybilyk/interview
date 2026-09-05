package com.conduct.interview._3_spring._2_bean_lifecycle.phases._10PreDestroy_Annotation;

import jakarta.annotation.PreDestroy;

public class PreDestroyAnnotation {

  @PreDestroy
  public void onDestroy() {}
}
