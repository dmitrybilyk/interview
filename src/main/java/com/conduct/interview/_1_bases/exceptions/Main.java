package com.conduct.interview._1_bases.exceptions;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            doSomething();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("dfdfd");
    }

    private static void doSomething() throws IOException {
        System.out.println("fdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdfdf");
//        try {
            doSomething2();
//        } catch (IOException e) {
//            System.out.println("dfdfd");
//        }
    }

    private static void doSomething2() throws IOException {
        System.out.println("fdfdfdfdfdfdfdfdf222222222222fdfdfdf");
        throw new IOException();
    }
}
