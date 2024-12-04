package com.conduct.interview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) {
//        List<String> stringList = List.of("one", "two", "three");
        List<String> list = new ArrayList<>();
//        list.add("one");
//        list.add("two");
//        list.add("three");
        List<String> stringList = Arrays.asList("one", "two", "three", "two", "two");

        List<String> value = null;
//        for(int i = 0; i < stringList.size(); i++) {
//            String item = stringList.get(i);
//            if (item.equals("two")) {
//                value.add(item);
//                continue;
//            }
//            System.out.println("fdfdff");
//            System.out.println("fdfdff");
//            System.out.println("fdfdff");
//        }
//        System.out.println(value);

//        for(String element: stringList) {
//            if (element.equals("two")) {
//                value.add(element);
//                continue;
//            }
//            System.out.println(element);
//        }


//        Studen st = new Student():

//
//        int a = 5;
////        while (a != 5) {
////            System.out.println(a);
////            a--;
////        }
//
//        do {
//            System.out.println(a);
//        } while (a != 5);
//        Consumer consumer = new Consumer() {
//            @Override
//            public void accept(Object o) {
//                System.out.println(o);
//            }
//        };

        stringList.forEach(myParam -> System.out.println(myParam));

        stringList.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });

    }
}
