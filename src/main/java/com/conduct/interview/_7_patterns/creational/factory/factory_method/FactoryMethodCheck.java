package com.conduct.interview._7_patterns.creational.factory.factory_method;

public class FactoryMethodCheck {
  public static void main(String[] args) {
    CocktailMaker cocktailMaker;
    String whatClientWants = "milk";
    if (whatClientWants.equals("hot")) {
      cocktailMaker = new HotCocktailMaker();
    } else if (whatClientWants.equals("milk")) {
      cocktailMaker = new MilkCocktailMaker();
    } else {
      cocktailMaker = new ColdCocktailMaker();
    }
    cocktailMaker.prepareForMaking();
    Cocktail cocktail = cocktailMaker.makeCocktail();
    System.out.println(cocktail.name);
  }
}

abstract class CocktailMaker {
  // could be much more logic common for both types of the product (cocktail)
  void prepareForMaking() {
    System.out.println("preparing for making");
  }

  abstract Cocktail makeCocktail();
}

class HotCocktailMaker extends CocktailMaker {
  public Cocktail makeCocktail() {
    System.out.println("hot coctail is created");
    Cocktail cocktail = new ColdCocktail();
    cocktail.name = "hot coctail";
    return cocktail;
  }
}

class ColdCocktailMaker extends CocktailMaker {
  public Cocktail makeCocktail() {
    System.out.println("cold coctail is created");
    Cocktail cocktail = new ColdCocktail();
    cocktail.name = "cold coctail";
    return cocktail;
  }
}

class MilkCocktailMaker extends CocktailMaker {
  public Cocktail makeCocktail() {
    System.out.println("milk coctail is created");
    Cocktail cocktail = new MilkCocktail();
    cocktail.name = "milk coctail";
    return cocktail;
  }
}

class Cocktail {
  public String name;
}

class HotCocktail extends Cocktail {}

class ColdCocktail extends Cocktail {}

class MilkCocktail extends Cocktail {}
