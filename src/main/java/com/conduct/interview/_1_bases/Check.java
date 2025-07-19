package com.conduct.interview._1_bases;

import java.io.IOException;

//import static java.lang.StringTemplate.STR;

public class Check {
    public static void main(String[] args) {
        SomeAbstractClass someInterface = new SomeClass();
//        someInterface.someMethod();
//        someInterface.otherMethod();
        someInterface.someAbstractMethod();

        String name = "fff";
//        String s = STR."Hi \{name}!";

        MyParentClass myParentClass = new MyChildClass();
        System.out.println(myParentClass.getName());
    }
}



class MyParentClass {
    private String name;

    public MyParentClass(String name) {
        this.name = name;
    }

    public MyParentClass() {
    }

    public String getName() {
        return name;
    }

    protected Number getNumber(Number number) throws Exception {
        return 20;
    }

}

class MyChildClass extends MyParentClass {

    public MyChildClass(String name) {
//        super(name);
    }

    public MyChildClass() {
    }

    @Override
    public Integer getNumber(Number number) throws IOException {
        return 55;
    }
}

class SomeClass extends SomeAbstractClass implements SomeInterface {

    private String name = "SomeClass";

    @Override
    public void someMethod() {
        System.out.println("fffff");
    }

    @Override
    public void otherMethod() {
        System.out.println(someConstant);
    }

    @Override
    void someAbstractMethod() {
        System.out.println("ffffff");
    }
}

interface SomeInterface {
    String someConstant = "Some";

    void someMethod();

    default void otherMethod() {
        System.out.println("dfdf");
        System.out.println(someConstant);
    }
}

abstract class SomeAbstractClass {
    private String someName = "SomeAbstractClass";
    abstract void someAbstractMethod();

    void print() {
        System.out.println(someName);
    }


}