package com.conduct.interview._7_patterns.creational.factory;

public class FactoryCheckFinalize {
    public static void main(String[] args) {
        boolean good = true;
        Member member = MemberFactory.createMember(good);
        member.print();

        // factory method check

        Club club = new NightClub();

        // factory
        FurnitureFactory factory = new FurnitureFactory();
        Furniture furniture = factory.createFurniture(FurnitureGoal.EAT);
        System.out.println(furniture);

        // abstract factory. Depending on some boolean

        boolean big = true;
        if (big) {
            AbstractFurnitureFactory furnitureFactory = new BigFurnitureFactory();
            System.out.println(furnitureFactory.createChair());
            System.out.println(furnitureFactory.createSofa());
            System.out.println(furnitureFactory.createTable());
        }

    }
}

abstract class Club {
    private String name;
    protected Member superMember;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    abstract Member createSuperMember();

    public void doCeremony() {
        System.out.println("ceremony is in progress");
    }

    public void payToMember(Member member) {
        System.out.println("Paying to " + member);
    }
}

class NightClub extends Club {

    @Override
    Member createSuperMember() {
        superMember = new BadMember();
        return superMember;
    }
}

class LibraryClub extends Club {

    @Override
    Member createSuperMember() {
        superMember = new GoodMember();
        return superMember;
    }
}

class MemberFactory {
    public static Member createMember(boolean good) {
        if (good) {
            return new GoodMember();
        } else {
            return new BadMember();
        }
    }
}

class Member {
    public void print() {
        System.out.println("member");
    }
}

class GoodMember extends Member {
    @Override
    public void print() {
        System.out.println("good member");
    }
}

class BadMember extends Member {
    @Override
    public void print() {
        System.out.println("bad member");
    }
}

class Furniture {

}
class Table extends Furniture{}

class Sofa extends Furniture{}

class Chair extends Furniture{}

enum FurnitureGoal{
    SEAT,
    EAT,
    REST
}

class FurnitureFactory {
    public Furniture createFurniture(FurnitureGoal furnitureGoal) {
        switch (furnitureGoal) {
            case EAT -> {
                return new Table();
            }
            case REST -> {
                return new Sofa();
            }
            case SEAT -> {
                return new Chair();
            }
        }
        return null;
    }
}

class BigTable extends Table {

}

class BigChair extends Chair {

}

class BigSofa extends Sofa {

}
class SmallTable extends Table {

}

class SmallChair extends Chair {

}

class SmallSofa extends Sofa {

}

abstract class AbstractFurnitureFactory {
    abstract Table createTable();
    abstract Chair createChair();
    abstract Sofa createSofa();
}

class BigFurnitureFactory extends AbstractFurnitureFactory {

    @Override
    Table createTable() {
        return new BigTable();
    }

    @Override
    Chair createChair() {
        return new BigChair();
    }

    @Override
    Sofa createSofa() {
        return new BigSofa();
    }
}

class SmallFurnitureFactory extends AbstractFurnitureFactory {

    @Override
    Table createTable() {
        return new SmallTable();
    }

    @Override
    Chair createChair() {
        return new SmallChair();
    }

    @Override
    Sofa createSofa() {
        return new SmallSofa();
    }
}

