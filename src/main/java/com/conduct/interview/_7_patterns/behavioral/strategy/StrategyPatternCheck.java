package com.conduct.interview._7_patterns.behavioral.strategy;

public class StrategyPatternCheck {
    public static void main(String[] args) {
        PrintingContext printingContext = new PrintingContext(new LowercaseStrategyImpl());
        printingContext.smartPrint("Some value");

        printingContext.setPrintingStrategy(value -> System.out.println(value + value));
        printingContext.smartPrint("ddd");
    }
}

class PrintingContext {
    private PrintingStrategy printingStrategy;

    public PrintingContext(PrintingStrategy printingStrategy) {
        this.printingStrategy = printingStrategy;
    }

    public void setPrintingStrategy(PrintingStrategy printingStrategy) {
        this.printingStrategy = printingStrategy;
    }

    void smartPrint(String value) {
        printingStrategy.print(value);
    }

}

interface PrintingStrategy {
    void print(String value);
}

class UppercaseStrategyImpl implements PrintingStrategy {

    @Override
    public void print(String value) {
        System.out.println(value.toUpperCase());
    }
}

class LowercaseStrategyImpl implements PrintingStrategy {

    @Override
    public void print(String value) {
        System.out.println(value.toLowerCase());
    }
}
