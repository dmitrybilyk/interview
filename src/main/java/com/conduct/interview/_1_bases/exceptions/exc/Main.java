package com.conduct.interview._1_bases.exceptions.exc;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int a = 10;
        int b = 0;
//        try {
//            System.out.println(a / b);
////        } catch (NullPointerException e) {
////            System.out.println(e);
////        } catch (ArithmeticException e) {
////            System.out.println(e);
//        } catch (RuntimeException e) {
//            System.out.println(e);
//        }
        try {
            someMethod("ffff");
//            return;
//            System.out.println("ffffffff");
        } catch (IOException e) {
//            System.out.println("fdfdf");
            throw new RuntimeException(e);
        } finally {
            throw new RuntimeException();
        }
//        System.out.println("next");
//        System.out.println("next2");
//        System.out.println("next3");
    }

    private static void someMethod(String s) throws IOException {
//        if (s.contains("DDD")) {
//            throw new ContainsDDDException("It should not contain DDD");
//        }
        System.out.println(s);
    }
}
