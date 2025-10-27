package com.conduct.interview._1_bases.java8.streams.map;

import java.util.List;
import lombok.Data;

@Data
public class Criteria {
  private String name;
  private List<Subevaluation> subevaluationList;
}
