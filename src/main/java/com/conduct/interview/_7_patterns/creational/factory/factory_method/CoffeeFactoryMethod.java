package com.conduct.interview._7_patterns.creational.factory.factory_method;

public class CoffeeFactoryMethod {

  public static void main(String[] args) {
    CoffeeShop coffeeShop = new CoffeeShop(new SimpleCoffeeFactory());

    Coffee coffee = coffeeShop.orderCoffee(CoffeeType.COLD_COFFEE);
    coffee.makeCoffee();
    coffee.grindCoffee();
    coffee.pourIntoCup();
    // System.out.println(coffee);
  }
}

class SimpleCoffeeFactory {
  public Coffee createCoffee(CoffeeType type) {
    Coffee coffee = null;

    switch (type) {
      case AMERICANO:
        coffee = new Americano();
        break;
      case ESPRESSO:
        coffee = new Espresso();
        break;
      case CAPPUCCINO:
        coffee = new Cappuccino();
        break;
      case CAFFE_LATTE:
        coffee = new CaffeLatte();
        break;
      case COLD_COFFEE:
        coffee = new ColdCoffee();
        break;
    }

    return coffee;
  }
}

class CoffeeShop {
  private final SimpleCoffeeFactory coffeeFactory;

  public CoffeeShop(SimpleCoffeeFactory coffeeFactory) {
    this.coffeeFactory = coffeeFactory;
  }

  public Coffee orderCoffee(CoffeeType type) {
    Coffee coffee = coffeeFactory.createCoffee(type);

    coffee.grindCoffee();
    coffee.makeCoffee();
    coffee.pourIntoCup();

    System.out.println("Вот ваш кофе! Спасибо, приходите еще!");
    return coffee;
  }
}

class Coffee {
  public void grindCoffee() {
    // перемалываем кофе
  }

  public void makeCoffee() {
    // делаем кофе
  }

  public void pourIntoCup() {
    // наливаем в чашку
  }
}

class Americano extends Coffee {}

class Cappuccino extends Coffee {}

class CaffeLatte extends Coffee {}

class Espresso extends Coffee {}

class ColdCoffee extends Coffee {
  @Override
  public void makeCoffee() {
    super.makeCoffee();
    System.out.println("Also making coffee cold");
  }
}

enum CoffeeType {
  ESPRESSO,
  AMERICANO,
  CAFFE_LATTE,
  CAPPUCCINO,
  COLD_COFFEE
}
