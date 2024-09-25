package com.conduct.interview._1_bases.generics;

import java.util.ArrayList;

public class Covariance {
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
  private static int totalWeight(ArrayList<? extends Orange> oranges) {
    //        !!!CAN READ - Type or parent types
    Orange orange = oranges.get(0);
    Fruit fruit = oranges.get(0);
    //        !!!CAN'T READ child types
    //        BigRoundOrange bigRoundOrange = oranges.get(0);
    //        !!!CAN'T add anything because we don't know the type and can break types safety
    //        oranges.add(new Object());
    //        oranges.add(new Orange());
    return 0;
  }

  public static void main(String[] args) {

    ArrayList<Orange> oranges = new ArrayList<>();
    ArrayList<BigRoundOrange> bigRoundOranges = new ArrayList<>();
    ArrayList<Fruit> fruits = new ArrayList<>();
    //        !!!CAN'T ASSIGN parent
    //        totalWeight(fruits);
    //        !!!CAN ASSIGN - can assign list of subtypes or the same type
    totalWeight(bigRoundOranges);
    totalWeight(oranges);
  }
}
