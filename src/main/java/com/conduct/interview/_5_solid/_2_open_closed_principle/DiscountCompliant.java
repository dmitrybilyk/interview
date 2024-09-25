package com.conduct.interview._5_solid._2_open_closed_principle;

// Step 1: Define a common interface for discounts
interface Discount {
  double apply(Order order);
}

// Step 2: Implement concrete discount types
class PercentageDiscount implements Discount {
  @Override
  public double apply(Order order) {
    return order.getTotal() * 0.9; // 10% discount
  }
}

class FixedAmountDiscount implements Discount {
  @Override
  public double apply(Order order) {
    return order.getTotal() - 20; // $20 discount
  }
}

class TotalAmountDiscount implements Discount {
  @Override
  public double apply(Order order) {
    return order.getTotal() - 100; // $20 discount
  }
}

// Step 3: Create an Order class
class Order {
  private double total;

  public Order(double total) {
    this.total = total;
  }

  public double getTotal() {
    return total;
  }
}

// Step 4: The DiscountService uses the Discount interface
class DiscountService {
  public double applyDiscount(Order order, Discount discount) {
    return discount.apply(order);
  }
}

// Usage
public class DiscountCompliant {
  public static void main(String[] args) {
    Order order = new Order(200);
    DiscountService discountService = new DiscountService();

    Discount percentageDiscount = new PercentageDiscount();
    System.out.println(discountService.applyDiscount(order, percentageDiscount)); // Output: 180.0

    Discount fixedDiscount = new FixedAmountDiscount();
    System.out.println(discountService.applyDiscount(order, fixedDiscount)); // Output: 180.0

    Discount totalDiscount = new TotalAmountDiscount();
    System.out.println(discountService.applyDiscount(order, totalDiscount)); // Output: 180.0
  }
}
