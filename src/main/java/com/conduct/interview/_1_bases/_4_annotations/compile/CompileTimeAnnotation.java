package com.conduct.interview._1_bases._4_annotations.compile;

public class CompileTimeAnnotation {
  public static void main(String[] args) {
    User user = new User();
    user.setName(null);
    System.out.println(user.getName());
  }
}
