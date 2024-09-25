package com.conduct.interview._1_bases.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Reflection {
  public static void main(String[] args)
      throws ClassNotFoundException,
          NoSuchMethodException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException,
          NoSuchFieldException {
    Class<?> clazz = Class.forName("com.conduct.interview._1_bases.reflection.ForReflection");
    Constructor<?> constructor = clazz.getConstructor(String.class);
    ForReflection o = (ForReflection) constructor.newInstance("ddd");
    System.out.println(o.getName());

    Class<?> clazz2 = ForReflection.class;
    Field field = clazz2.getDeclaredField("name");
    field.setAccessible(true);

    ForReflection forReflection = new ForReflection("my name");

    System.out.println(field.get(forReflection));

    field.set(forReflection, "new name");
    System.out.println(field.get(forReflection));
  }
}

class ForReflection {
  private String name;

  public ForReflection(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  private int someMethod(double d) {
    return 30;
  }
}
