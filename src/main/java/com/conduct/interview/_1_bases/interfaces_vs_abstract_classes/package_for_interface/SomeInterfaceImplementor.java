package com.conduct.interview._1_bases.interfaces_vs_abstract_classes.package_for_interface;

public class SomeInterfaceImplementor implements SomeInterface {
    private String name;
    @Override
    public void intPrint() {

    }

    @Override
    public void printDefault() {
        System.out.println(name);
    }
}
