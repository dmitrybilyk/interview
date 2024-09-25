package com.conduct.interview._7_patterns.creational.builder;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

public class BuilderCheck {
  public static void main(String[] args) {
    RealEstimateDirector realEstimateDirector = new RealEstimateDirector();
    HouseBuilder houseBuilder = new HouseBuilder();
    realEstimateDirector.createHouseBuilder(houseBuilder);
    System.out.println(houseBuilder.getResult());
  }
}

@Data
@AllArgsConstructor
class House {
  private String address;
  private int numberOfRooms;
  private BigDecimal cost;
}

@Data
@AllArgsConstructor
class Flat {
  private String address;
  private int numberOfRooms;
  private BigDecimal cost;
}

interface RealEstateBuilder {
  void setAddress(String address);

  void setNumberOfRooms(int numberOfRooms);

  void setCost(BigDecimal cost);
}

class HouseBuilder implements RealEstateBuilder {
  private String address;
  private int numberOfRooms;
  private BigDecimal cost;

  @Override
  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public void setNumberOfRooms(int numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
  }

  @Override
  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

  public House getResult() {
    return new House(address, numberOfRooms, cost);
  }

  @Override
  public String toString() {
    return "HouseBuilder{"
        + "address='"
        + address
        + '\''
        + ", numberOfRooms="
        + numberOfRooms
        + ", cost="
        + cost
        + '}';
  }
}

class FlatBuilder implements RealEstateBuilder {
  private String address;
  private int numberOfRooms;
  private BigDecimal cost;

  @Override
  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public void setNumberOfRooms(int numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
  }

  @Override
  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

  public Flat getResult() {
    return new Flat(address, numberOfRooms, cost);
  }

  @Override
  public String toString() {
    return "FlatBuilder{"
        + "address='"
        + address
        + '\''
        + ", numberOfRooms="
        + numberOfRooms
        + ", cost="
        + cost
        + '}';
  }
}

@Data
class RealEstimateDirector {

  public HouseBuilder createHouseBuilder(HouseBuilder houseBuilder) {
    houseBuilder.setAddress("House address");
    houseBuilder.setCost(BigDecimal.TEN);
    houseBuilder.setNumberOfRooms(3);
    return houseBuilder;
  }

  public FlatBuilder createFlatBuilder(FlatBuilder flatBuilder) {
    flatBuilder.setAddress("Flat address");
    flatBuilder.setCost(BigDecimal.ONE);
    flatBuilder.setNumberOfRooms(2);
    return flatBuilder;
  }
}
