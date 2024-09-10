//package com.learn.easyboot.patterns.creational.factory.abstract_factory;
//
//public class AbstractFactoryCheck2 {
//    public static void main(String[] args) {
//        CoffeeMaker coffeeMaker = new CappuccinoCoffeeMaker(new BigCaffeAbstractFactory());
//        coffeeMaker.makeACoffee();
//    }
//}
//
//abstract class CoffeeMaker {
//    protected CaffeAbstractFactory caffeAbstractFactory;
//
//    public CoffeeMaker(CaffeAbstractFactory caffeAbstractFactory) {
//        this.caffeAbstractFactory = caffeAbstractFactory;
//    }
//
//    abstract Coffee makeACoffee();
//}
//
//class CappuccinoCoffeeMaker extends CoffeeMaker {
//
//    public CappuccinoCoffeeMaker(CaffeAbstractFactory caffeAbstractFactory) {
//        super(caffeAbstractFactory);
//    }
//
//    @Override
//    Coffee makeACoffee() {
//        return caffeAbstractFactory. Cappuccino();
//    }
//}
//
//
//class LatteCoffeeMaker extends CoffeeMaker {
//
//    public LatteCoffeeMaker(CaffeAbstractFactory caffeAbstractFactory) {
//        super(caffeAbstractFactory);
//    }
//
//    @Override
//    Coffee makeACoffee() {
//        return new Latte();
//    }
//}
//
//class Coffee {}
//class Cappuccino extends Coffee {}
//class Latte extends Coffee {}
//
//
//class CaffeAbstractFactory {
//
//}
//
//class BigCaffeAbstractFactory extends CaffeAbstractFactory {}
//
//class SmallCaffeAbstractFactory extends CaffeAbstractFactory{}
