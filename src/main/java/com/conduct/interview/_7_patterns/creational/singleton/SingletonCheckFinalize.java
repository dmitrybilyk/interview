package com.conduct.interview._7_patterns.creational.singleton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SingletonCheckFinalize {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        StaticClassSingleton singleton = StaticClassSingleton.getInstance();
        StaticClassSingleton singleton2 = StaticClassSingleton.getInstance();
        System.out.println(singleton);
        System.out.println(singleton2);


        //breaking with reflection

//        StaticClassSingleton s1 = StaticClassSingleton.getInstance();
//
//        Constructor<StaticClassSingleton> constructor =
//                StaticClassSingleton.class.getDeclaredConstructor();
//        constructor.setAccessible(true); // bypass private
//
//        StaticClassSingleton s2 = constructor.newInstance();
//
//        System.out.println("Singleton from getInstance(): " + s1);
//        System.out.println("Singleton via reflection:   " + s2);
//        System.out.println("Same instance? " + (s1 == s2));

        // break with serialization

        StaticClassSingleton s1 = StaticClassSingleton.getInstance();

        // Serialize the instance
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("singleton.obj"));
        out.writeObject(s1);
        out.close();

        // Deserialize the instance
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("singleton.obj"));
        StaticClassSingleton s2 = (StaticClassSingleton) in.readObject();
        in.close();

        System.out.println("Singleton from getInstance(): " + s1);
        System.out.println("Singleton via serialization: " + s2);
        System.out.println("Same instance? " + (s1 == s2));


        EnumClassSingleton enumClassSingleton = EnumClassSingleton.INSTANCE;
        System.out.println(enumClassSingleton.getAge());
        enumClassSingleton.doSomething();
    }
}

class StaticClassSingleton implements Serializable {
    private StaticClassSingleton() {
        if (SingletonHolder.INSTANCE != null) {
            throw new RuntimeException();
        }
    }

    static class SingletonHolder {
        static StaticClassSingleton INSTANCE = new StaticClassSingleton();
    }

    public static StaticClassSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // Protect against serialization
    private Object readResolve() {
        return getInstance();
    }
}

enum EnumClassSingleton {
    INSTANCE;

    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void doSomething() {
        System.out.println("dfdfd");
    }
}