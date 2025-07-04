package com.conduct.interview._1_bases._1_passing_params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class PassingParams {
  public static void main(String[] args) {
    ChildForPassing childForPassing = new ChildForPassing();
    childForPassing.setName("childForPassing Original");
    ForPassingParams forPassingParams = new ForPassingParams("someString", childForPassing);
    int a = 10;
    passParams(a, forPassingParams);
    System.out.println(a);
    System.out.println(forPassingParams.getNamePassing());
  }

  private static void passParams(int a, ForPassingParams forPassingParams) {
    // no effect because parameter is primitive is passed BY VALUE
    a = 230;
    forPassingParams.setNamePassing("new name");
    // no effect because the object is passed BY VALUE too
    forPassingParams = new ForPassingParams("new String name", new ChildForPassing());
  }
}

@Getter
@Setter
@AllArgsConstructor
class ForPassingParams {
  private String namePassing;
  private ChildForPassing childForPassing;
}

@Getter
@Setter
class ChildForPassing {
  private String name;
}
