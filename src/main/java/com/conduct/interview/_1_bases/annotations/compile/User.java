package com.conduct.interview._1_bases.annotations.compile;

public class User {

  @NotNull(message = "Name must not be null")
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
