package com.conduct.interview._5_solid.open_closed_principle;

import lombok.Getter;
import lombok.Setter;

public class Compliant {

    public static void main(String[] args) {
        MyCalculator myCalculator = new MyCalculator();
        AdditionOperation operation = new AdditionOperation(3, 3);
        myCalculator.calculate(operation);
        System.out.println(operation.getResult());
    }

}

interface Operation {
    void perform();
}

class MyCalculator {
    public void calculate(Operation operation) {
        operation.perform();
    }
}

@Getter
@Setter
class SubtractionOperation implements Operation{
    private double left;
    private double right;
    private double result = 0.0;

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
class AdditionOperation implements Operation {
    private double left;
    private double right;
    private double result = 0.0;

    public AdditionOperation(double left, double right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void perform() {
        result = left + right;
    }
}