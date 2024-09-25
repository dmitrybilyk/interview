package com.conduct.interview._7_patterns.behavioral.state;

public class StatePatternCheck5 {
  public static void main(String[] args) {
    Developer developer = new Developer();
    developer.clickPowerButton();
    developer.clickPowerButton();
    developer.clickPowerButton();
    developer.clickStandByButton();
    developer.clickAnyButton();
    developer.clickAnyButton();
    developer.clickPowerButton();
  }
}

class Developer {
  private LaptopState laptopState = new PoweredOffState(this);

  public Developer() {}

  public Developer(LaptopState laptopState) {
    this.laptopState = laptopState;
  }

  public void clickPowerButton() {
    laptopState.clickPowerButton();
  }

  public void clickStandByButton() {
    laptopState.clickStandByButton();
  }

  public void clickAnyButton() {
    laptopState.clickAnyButton();
  }

  public void setLaptopState(LaptopState laptopState) {
    this.laptopState = laptopState;
  }
}

abstract class LaptopState {
  protected Developer context;

  public LaptopState(Developer context) {
    this.context = context;
  }

  abstract void clickAnyButton();

  abstract void clickPowerButton();

  abstract void clickStandByButton();
}

class PoweredOffState extends LaptopState {

  public PoweredOffState(Developer context) {
    super(context);
  }

  @Override
  void clickAnyButton() {
    System.out.println("Nothing happens");
  }

  @Override
  void clickPowerButton() {
    System.out.println("Turning on the laptop");
    context.setLaptopState(new PoweredOnState(context));
  }

  @Override
  void clickStandByButton() {
    System.out.println("Nothing happens");
  }
}

class PoweredOnState extends LaptopState {

  public PoweredOnState(Developer context) {
    super(context);
  }

  @Override
  void clickAnyButton() {
    System.out.println("Something happens");
  }

  @Override
  void clickPowerButton() {
    System.out.println("Turning off the laptop");
    context.setLaptopState(new PoweredOffState(context));
  }

  @Override
  void clickStandByButton() {
    System.out.println("Going to Stand By");
    context.setLaptopState(new StandByState(context));
  }
}

class StandByState extends LaptopState {

  public StandByState(Developer context) {
    super(context);
  }

  @Override
  void clickAnyButton() {
    System.out.println("Awaking the laptop");
    context.setLaptopState(new PoweredOnState(context));
  }

  @Override
  void clickPowerButton() {
    System.out.println("Awaking the laptop");
  }

  @Override
  void clickStandByButton() {
    System.out.println("Nothing happens");
  }
}
