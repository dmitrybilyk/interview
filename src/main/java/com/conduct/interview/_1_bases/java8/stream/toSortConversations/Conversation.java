package com.conduct.interview._1_bases.java8.stream.toSortConversations;

import java.time.Instant;

public class Conversation {
  private String name;
  private Instant startDate;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Instant getStartDate() {
    return startDate;
  }

  public void setStartDate(Instant startDate) {
    this.startDate = startDate;
  }
}
