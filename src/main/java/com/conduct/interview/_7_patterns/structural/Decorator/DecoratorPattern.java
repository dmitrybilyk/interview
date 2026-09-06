package com.conduct.interview._7_patterns.structural.decorator;

public class DecoratorPattern {
  public static void main(String[] args) {
    TShirtProducer tShirtProducer =
        new WaterProofTShirtDecorator(
            new ColorfulTShirtDecorator(
                				new SimpleTShirtProducer()
                ));
    tShirtProducer.produceTShirt();
  }
}

interface TShirtProducer {
  void produceTShirt();
}

class SimpleTShirtProducer implements TShirtProducer {
  public void produceTShirt() {
    System.out.println("Simple tshirt is produced");
  }
}

class BaseTShirtProducerDecorator implements TShirtProducer {
  protected TShirtProducer tShirtProducer;

  public BaseTShirtProducerDecorator(TShirtProducer tShirtProducer) {
    this.tShirtProducer = tShirtProducer;
  }

  public BaseTShirtProducerDecorator() {}

  public void produceTShirt() {
    tShirtProducer.produceTShirt();
  }
}

class ColorfulTShirtDecorator extends BaseTShirtProducerDecorator {
  public ColorfulTShirtDecorator(TShirtProducer tShirtProducer) {
    super(tShirtProducer);
  }

  public ColorfulTShirtDecorator() {
    super();
  }

  public void produceTShirt() {
    super.produceTShirt();
    System.out.println("Added color");
  }
}

class WaterProofTShirtDecorator extends BaseTShirtProducerDecorator {
  public WaterProofTShirtDecorator(TShirtProducer tShirtProducer) {
    super(tShirtProducer);
  }

  public void produceTShirt() {
    super.produceTShirt();
    System.out.println("Added water proof feature");
  }
}
