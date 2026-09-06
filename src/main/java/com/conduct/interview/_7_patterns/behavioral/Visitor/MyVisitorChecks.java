package com.conduct.interview._7_patterns.behavioral.visitor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class MyVisitorChecks {
    public static void main(String[] args) {
        ImprovementVisitor improvementVisitor = new ImprovementVisitorImpl();
        ChairFurnitureElement chaiFurnitureElement = new ChairFurnitureElement();
        chaiFurnitureElement.accept(improvementVisitor);
        System.out.println(chaiFurnitureElement.getChair());

    }
}

interface FurnitureElement {
    void accept(ImprovementVisitor improvementVisitor);
}

@ToString
@Getter
@Setter
class ChairFurnitureElement implements FurnitureElement {
    private String chair = "chair";
    @Override
    public void accept(ImprovementVisitor improvementVisitor) {
        improvementVisitor.visitAdd(this);
        improvementVisitor.visitColor(this);
        improvementVisitor.visitWeight(this);
    }
}

@ToString
@Getter
@Setter
class TableFurnitureElement implements FurnitureElement {
    private String table = "table";
    @Override
    public void accept(ImprovementVisitor improvementVisitor) {
        improvementVisitor.visitAdd(this);
        improvementVisitor.visitColor(this);
        improvementVisitor.visitWeight(this);
    }
}

interface ImprovementVisitor {
    void visitColor(ChairFurnitureElement chairFurnitureElement);
    void visitColor(TableFurnitureElement tableFurnitureElement);
    void visitAdd(ChairFurnitureElement chairFurnitureElement);
    void visitAdd(TableFurnitureElement tableFurnitureElement);
    void visitWeight(ChairFurnitureElement chairFurnitureElement);
    void visitWeight(TableFurnitureElement tableFurnitureElement);
}

class ImprovementVisitorImpl implements ImprovementVisitor {

    @Override
    public void visitColor(ChairFurnitureElement chairFurnitureElement) {
        chairFurnitureElement.setChair(chairFurnitureElement.getChair() + "colorful");
    }

    @Override
    public void visitColor(TableFurnitureElement tableFurnitureElement) {
        tableFurnitureElement.setTable(tableFurnitureElement.getTable() + "colorful");
    }

    @Override
    public void visitAdd(ChairFurnitureElement chairFurnitureElement) {
        System.out.println("advertased " + chairFurnitureElement);
    }

    @Override
    public void visitAdd(TableFurnitureElement tableFurnitureElement) {
        System.out.println("advertased " + tableFurnitureElement);
    }

    @Override
    public void visitWeight(ChairFurnitureElement chairFurnitureElement) {
        System.out.println("weight for " + chairFurnitureElement.getChair());
    }

    @Override
    public void visitWeight(TableFurnitureElement tableFurnitureElement) {
        System.out.println("weight for " + tableFurnitureElement.getTable());
    }
}
