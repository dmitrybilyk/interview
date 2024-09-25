package com.conduct.interview._5_solid._2_open_closed_principle;

import lombok.Getter;
import lombok.Setter;

public class Compliant {

  public static void main(String[] args) {
    MyCalculator myCalculator = new MyCalculator();
    Operation operation = new SubtractionOperation(3, 3);
    myCalculator.calculate(operation);
    System.out.println(operation.getResult());
  }
}

@Getter
@Setter
abstract class Operation {
  protected double result;

  abstract void perform();

  double getResult() {
    return result;
  }
  ;
}

class MyCalculator {
  public void calculate(Operation operation) {
    operation.perform();
  }
}

@Getter
@Setter
class SubtractionOperation extends Operation {
  private double left;
  private double right;

  public SubtractionOperation(double left, double right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public void perform() {
    result = left - right;
  }
}

@Getter
@Setter
class AdditionOperation extends Operation {
  private double left;
  private double right;

  public AdditionOperation(double left, double right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public void perform() {
    result = left + right;
  }
}

@Getter
@Setter
class SubtractOperation extends Operation {
  private double left;
  private double right;

  public SubtractOperation(double left, double right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public void perform() {
    result = left - right;
  }
}
