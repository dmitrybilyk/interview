package com.conduct.interview._7_patterns.behavioral.strategy;

import java.io.IOException;

public class StrategyPattern {
  private static Order order = new Order();
  private static PayStrategy strategy;

  public static void main(String[] args) throws IOException {
    order.setTotalCost(2000);
    String paymentMethod = "1";

    // Client creates different strategies based on input from user,
    // application configuration, etc.
    if (paymentMethod.equals("1")) {
      strategy = new PayByPayPal();
    } else {
      strategy = new PayByCreditCard();
    }

    // Order object delegates gathering payment data to strategy object,
    // since only strategies know what data they need to process a
    // payment.
    order.processOrder(strategy);

    System.out.print("Pay " + order.getTotalCost() + " units or Continue shopping? P/C: ");
    if (strategy.pay(order.getTotalCost())) {
      System.out.println("Payment has been successful.");
    } else {
      System.out.println("FAIL! Please, check your data.");
    }
  }
}

/** Common interface for all strategies. */
interface PayStrategy {
  boolean pay(int paymentAmount);

  void collectPaymentDetails();
}

/** Concrete strategy. Implements PayPal payment method. */
class PayByPayPal implements PayStrategy {
  /** Collect customer's data. */
  @Override
  public void collectPaymentDetails() {
    System.out.println("Data verification has been successful.");
  }

  /** Save customer data for future shopping attempts. */
  @Override
  public boolean pay(int paymentAmount) {
    System.out.println("Paying " + paymentAmount + " using PayPal.");
    return true;
  }
}

/** Concrete strategy. Implements credit card payment method. */
class PayByCreditCard implements PayStrategy {
  private CreditCard card;

  /** Collect credit card data. */
  @Override
  public void collectPaymentDetails() {
    System.out.print("Enter the card number: ");
  }

  /** After card validation we can charge customer's credit card. */
  @Override
  public boolean pay(int paymentAmount) {
    System.out.println("Paying " + paymentAmount + " using Credit Card.");
    return true;
  }
}

/** Dummy credit card class. */
class CreditCard {
  private int amount;
  private String number;
  private String date;
  private String cvv;

  CreditCard(String number, String date, String cvv) {
    this.amount = 100_000;
    this.number = number;
    this.date = date;
    this.cvv = cvv;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public int getAmount() {
    return amount;
  }
}

/**
 * Order class. Doesn't know the concrete payment method (strategy) user has picked. It uses common
 * strategy interface to delegate collecting payment data to strategy object. It can be used to save
 * order to database.
 */
class Order {
  private int totalCost = 0;

  public void processOrder(PayStrategy strategy) {
    strategy.collectPaymentDetails();
    // Here we could collect and store payment data from the strategy.
  }

  public void setTotalCost(int cost) {
    this.totalCost += cost;
  }

  public int getTotalCost() {
    return totalCost;
  }
}
