package com.conduct.interview._7_patterns.behavioral.visitor;

import org.apache.catalina.Host;

import java.util.logging.Handler;

public class VisitorCheck {
    public static void main(String[] args) {
        BuildVisitor buildVisitor = new ActualBuildVisitorImpl();
        BuildVisitor buildBetterVisitor = new ActualBetterBuildVisitorImpl();
        BuildItem buildBarn = new BuildBarn();
        buildBarn.accept(buildVisitor);
        BuildItem hotel = new BuildHotel();
        hotel.accept(buildVisitor);
        hotel.accept(buildBetterVisitor);
    }
}

interface BuildVisitor {
    void visit(BuildFlat buildFlat);
    void visit(BuildHotel buildHotel);
    void visit(BuildBarn buildBarn);
}

class ActualBuildVisitorImpl implements BuildVisitor {

    @Override
    public void visit(BuildFlat buildFlat) {
        System.out.println("building Flat");
    }

    @Override
    public void visit(BuildHotel buildHotel) {
        System.out.println("Building hotel");
    }

    @Override
    public void visit(BuildBarn buildBarn) {
        System.out.println("Building Barn");
    }
}

class ActualBetterBuildVisitorImpl implements BuildVisitor {

    @Override
    public void visit(BuildFlat buildFlat) {
        System.out.println("building better Flat");
    }

    @Override
    public void visit(BuildHotel buildHotel) {
        System.out.println("Building better hotel");
    }

    @Override
    public void visit(BuildBarn buildBarn) {
        System.out.println("Building better Barn");
    }
}

interface BuildItem {
    void accept(BuildVisitor visitor);
}

class BuildFlat implements BuildItem {

    @Override
    public void accept(BuildVisitor visitor) {
        visitor.visit(this);
    }
}

class BuildHotel implements BuildItem {

    @Override
    public void accept(BuildVisitor visitor) {
        visitor.visit(this);
    }
}

class BuildBarn implements BuildItem {

    @Override
    public void accept(BuildVisitor visitor) {
        visitor.visit(this);
    }
}