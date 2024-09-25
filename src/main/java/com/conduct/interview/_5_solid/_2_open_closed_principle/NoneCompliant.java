package com.conduct.interview._5_solid._2_open_closed_principle;

import java.security.InvalidParameterException;
import lombok.Getter;
import lombok.Setter;

public class NoneCompliant {

  public static void main(String[] args) {
    Calculator calculator = new Calculator();
    Addition operation = new Addition(4d, 6d);
    calculator.calculate(operation);
    System.out.println(operation.getResult());
  }
}

interface CalculatorOperation {}

class Calculator {
  public void calculate(CalculatorOperation operation) {
    if (operation == null) {
      throw new InvalidParameterException("Can not perform operation");
    }

    if (operation instanceof Addition) {
      Addition addition = (Addition) operation;
      addition.setResult(addition.getLeft() + addition.getRight());
    } else if (operation instanceof Subtraction) {
      Subtraction subtraction = (Subtraction) operation;
      subtraction.setResult(subtraction.getLeft() - subtraction.getRight());
    }
  }
}

@Getter
@Setter
class Subtraction implements CalculatorOperation {
  private double left;
  private double right;
  private double result = 0.0;

  public Subtraction(double left, double right) {
    this.left = left;
    this.right = right;
  }
}

@Getter
@Setter
class Addition implements CalculatorOperation {
  private double left;
  private double right;
  private double result = 0.0;

  public Addition(double left, double right) {
    this.left = left;
    this.right = right;
  }
}
