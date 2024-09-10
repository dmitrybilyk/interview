package com.conduct.interview._1_bases.generics;

import java.util.ArrayList;

public class Invariance {
    static class Fruit {
        int weight;
    }
    static class Citrus extends Fruit {
        int weight;
    }
    static class Orange extends Citrus {
        int color;
    }
    static class BigRoundOrange extends Orange {
        int size = 100;
    }

//    private static int totalWeight(ArrayList<Orange> oranges) {
    private static int totalWeight(ArrayList<Orange> oranges) {
//        !!!CAN READ - Type or parent types
        Orange orange = oranges.get(0);
        Fruit fruit = oranges.get(0);
//        !!!CAN'T READ child classes
//        BigRoundOrange bigRoundOrange = oranges.get(0);

        //        !!!CAN ADD - Type or subtypes
        oranges.add(new Orange());
        oranges.add(new BigRoundOrange());
        //        !!!CAN'T ADD parent
//        oranges.add(new Citrus());
        return 0;
    }

    public static void main(String[] args) {

//        This is an invariance - we can assign to list just a list of the same type. Otherwise it's unsafe
//        !!!CAN ASSIGN - can assign just to a list of the same type
        ArrayList<Orange> oranges = new ArrayList<Orange>();
//        ArrayList<BigRoundOrange> bigRoundOranges = new ArrayList<>();
//        totalWeight(bigRoundOranges);
        totalWeight(oranges);

    }
}
