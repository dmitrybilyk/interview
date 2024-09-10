package com.conduct.interview._5_solid.liskov_principle.rules;

import java.io.FileNotFoundException;

public class ExceptionsSignatureRule {
    public static void main(String[] args) throws FileNotFoundException {
        Parent3 p = new Child3();
        p.someMethod(new SomeChild3());
    }

}

class Parent3 {
    protected SomeParent3 someMethod(SomeParent3 someParent) throws FileNotFoundException{
        return new SomeParent3();
    }
}

class Child3 extends Parent3 {
    @Override
    public SomeChild3 someMethod(SomeParent3 someParent) throws RuntimeException, FileNotFoundException {
        return new SomeChild3();
//        super.someMethod(someParent);
    }
}

class SomeParent3{}
class SomeChild3 extends SomeParent3{}