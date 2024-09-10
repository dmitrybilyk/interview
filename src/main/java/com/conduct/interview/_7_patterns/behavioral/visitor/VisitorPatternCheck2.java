package com.conduct.interview._7_patterns.behavioral.visitor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class VisitorPatternCheck2 {
    public static void main(String[] args) {
        Laptop laptop = new AsusLaptop("My New Asus", "Grey", "I7", 6);
        System.out.println(laptop.getDescription());
        laptop.accept(new UpgradeLapTopVisitor());
        System.out.println(laptop.getDescription());
        laptop.accept(new ChangeKeyboardLapTopVisitor());
        System.out.println(laptop.getDescription());
    }
}

@AllArgsConstructor
abstract class Laptop {
    protected String name;
    protected String color;
    protected String processor;
    protected int numberOfCores;

    abstract String getDescription();

    abstract void accept(LapTopVisitor visitor);
}

@Getter
@Setter
class AsusLaptop extends Laptop {
    private String asusFeatures = "some initial asus features";

    public AsusLaptop(String name, String color, String processor, int numberOfCores) {
        super(name, color, processor, numberOfCores);
    }

    @Override
    String getDescription() {
        return "ASUS. Name: " + name +
                "; Color: " +
                color + " ; Processor: " + processor +
                "; Number Of Cores: " + numberOfCores +
                "; Features: " + asusFeatures;
    }

    @Override
    void accept(LapTopVisitor visitor) {
        visitor.visitAsus(this);
        visitor.visitAll(this);
    }
}
class MacBookLaptop extends Laptop {

    public MacBookLaptop(String name, String color, String processor, int numberOfCores) {
        super(name, color, processor, numberOfCores);
    }

    @Override
    String getDescription() {
        return "MacBook. Name: " + name +
                "; Color: " +
                color + " ; Processor: " + processor +
                "; Number Of Cores: " + numberOfCores;
    }

    @Override
    void accept(LapTopVisitor visitor) {
        visitor.visitMacBook(this);
        visitor.visitAll(this);
    }
}

abstract class LapTopVisitor {
    abstract void visitAll(Laptop laptop);
    abstract void visitAsus(AsusLaptop laptop);
    abstract void visitMacBook(MacBookLaptop laptop);
}

class UpgradeLapTopVisitor extends LapTopVisitor {

    @Override
    void visitAll(Laptop laptop) {
        laptop.numberOfCores = laptop.numberOfCores * 2;
        laptop.name = laptop.name + " upgraded commonly";
    }

    @Override
    void visitAsus(AsusLaptop laptop) {
        laptop.name = laptop.name + " upgraded specifically";
    }
    @Override
    void visitMacBook(MacBookLaptop laptop) {
        laptop.name = laptop.name + " upgraded specifically";
    }
}
class ChangeKeyboardLapTopVisitor extends LapTopVisitor {

    @Override
    void visitAll(Laptop laptop) {
        System.out.println("Preparation to change the keyboard can be don on this model");
    }

    @Override
    void visitAsus(AsusLaptop laptop) {
        laptop.name = laptop.name + " changed the keyboard";
    }
    @Override
    void visitMacBook(MacBookLaptop laptop) {
        laptop.name = laptop.name + " changed the keyboard";
    }
}

class ChangeColorLapTopVisitor extends LapTopVisitor {
    private String newColor;
    public ChangeColorLapTopVisitor(String red) {
        this.newColor = red;
    }

    @Override
    void visitAll(Laptop laptop) {
        laptop.color = newColor;
    }

    @Override
    void visitAsus(AsusLaptop laptop) {
        laptop.color = newColor;
    }

    @Override
    void visitMacBook(MacBookLaptop laptop) {
        laptop.color = newColor;
    }
}

class UpdateFeaturesLapTopVisitor extends LapTopVisitor {
    public UpdateFeaturesLapTopVisitor() {
    }

    @Override
    void visitAll(Laptop laptop) {
        System.out.println("Just Asus features can be updated");
    }

    @Override
    void visitAsus(AsusLaptop laptop) {
        laptop.setAsusFeatures("Updated Features");
    }

    @Override
    void visitMacBook(MacBookLaptop laptop) {
        System.out.println("There is no such field");
    }
}
