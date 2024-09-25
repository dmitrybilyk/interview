package com.conduct.interview._3_spring.bean_lifecycle.phases._12Custom_Destruction_Method;

import lombok.Getter;

@Getter
public class CustomDestroyMethod {
  private String name;

  public void customDestroy() {
    System.out.println(
        "Custom Destruction: " + getName() + " is bidding farewell and performing a final action.");
    sayGoodbye();
    performFinalAction();
  }

  private void sayGoodbye() {
    System.out.println("Custom Destruction: " + getName() + " says goodbye.");
  }

  private void performFinalAction() {
    System.out.println("Custom Destruction: " + getName() + " performs a final action.");
  }
}
