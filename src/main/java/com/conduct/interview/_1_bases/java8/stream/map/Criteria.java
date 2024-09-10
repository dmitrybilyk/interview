package com.conduct.interview._1_bases.java8.stream.map;

import lombok.Data;

import java.util.List;

@Data
public class Criteria {
  private String name;
  private List<Subevaluation> subevaluationList;
}
