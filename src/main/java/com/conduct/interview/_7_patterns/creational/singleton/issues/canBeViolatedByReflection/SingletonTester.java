package com.conduct.interview._7_patterns.creational.singleton.issues.canBeViolatedByReflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SingletonTester {
  public static void main(String[] args) {
    // Create the 1st instance
    SingletonClass instance1 = SingletonClass.getInstance();

    // Create 2nd instance using Java Reflection API.
    SingletonClass instance2 = null;
    try {
      Class<SingletonClass> clazz = SingletonClass.class;
      Constructor<SingletonClass> cons = clazz.getDeclaredConstructor();
      cons.setAccessible(true);
      instance2 = cons.newInstance();
    } catch (NoSuchMethodException
        | InvocationTargetException
        | IllegalAccessException
        | InstantiationException e) {
      e.printStackTrace();
    }

    // now lets check the hash key.
    System.out.println("Instance 1 hash:" + instance1.hashCode());
    System.out.println("Instance 2 hash:" + instance2.hashCode());
  }
}

class SingletonClass {

  private static SingletonClass sSoleInstance;

  private SingletonClass() {
    // SOLUTION >>> Prevent form the reflection api.
    if (sSoleInstance != null) {
      throw new RuntimeException(
          "Use getInstance() method to get the single instance of this class.");
    }
  } // private constructor.

  public static SingletonClass getInstance() {
    if (sSoleInstance == null) { // if there is no instance available... create new one
      sSoleInstance = new SingletonClass();
    }

    return sSoleInstance;
  }
}
