package com.conduct.interview._5_solid.liskov_principle.rules;

public class MethodArgumentsSignatureRule {
    public static void main(String[] args) {
        Parent p = new Child();
        p.someMethod(new SomeChild());
    }

}

class Parent {
    protected void someMethod(SomeParent someParent) {

    }
}

class Child extends Parent {
    @Override
    public void someMethod(SomeParent someParent) {
//        super.someMethod(someParent);
    }
}

class SomeParent{}
class SomeChild extends SomeParent{}