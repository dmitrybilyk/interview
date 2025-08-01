package com.conduct.interview._1_bases.generics;

import java.util.ArrayList;
import java.util.List;

public class GenericClass<T> {
    public void print(T value) {
        System.out.println(value);
    }

    public void genericMethod(T value) {
        System.out.println(value);
    }

    public static void main(String[] args) {
        GenericClass<Boogie> genericClass = new GenericClass<>();
        genericClass.print(new Boogie("bname"));
        genericClass.genericMethod(new Boogie("bname"));

        List<ParentBookie> parentBookies = List.of(new ChildBookie());
        List<ChildBookie> childBookies = new ArrayList<>(List.of(new ChildBookie()));

        doSomethingWithGenerics(childBookies, parentBookies);

    }

    private static void doSomethingWithGenerics(List<? extends ParentBookie> bookies, List<ParentBookie> parentBookies) {
        ParentBookie parentBookie = bookies.get(0);

        ChildBookie childBookie = new ChildBookie();
        ChildishBookie childishBookie = new ChildishBookie();
        parentBookies.add(childBookie);
        parentBookies.add(childishBookie);

        System.out.println(parentBookie);


        List<ParentBookie> parentList = new ArrayList<>();
        List<Object> objectList = new ArrayList<>();
        List<ChildBookie> childList = new ArrayList<>();

        addBookies(parentList);
        addBookies(objectList);
        addBookies(childList);

    }

    static void addBookies(List<? super ChildBookie> list) {
        list.add(new ChildBookie());       // ✅ OK
        list.add(new ChildishBookie());    // ✅ OK
//         list.add(new ParentBookie());   // ❌ NOT allowed — too general
    }
}



record Boogie(String name) {}

class ParentBookie{
    protected String inheritedName;
}

class ChildBookie extends ParentBookie {

}

class ChildishBookie extends ChildBookie {

}
