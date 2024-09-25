package com.conduct.interview._5_solid._2_open_closed_principle;

public class CoffeeCompliant {
  public static void main(String[] args) {}
}

class Coffee {
  private int weight;
}

interface PrepareCoffee {}

class Cappuchino implements PrepareCoffee {}

class Latte implements PrepareCoffee {}
