package com.conduct.interview._1_bases.generics.udemy;

public class Zoo {

//    public void feed(Cage<? extends Animal> cage) {  // Covariant param: accepts Cage<Lion>, Cage<Monkey>, etc.
//        cage.setAnimal1(new Monkey());  // Compile ERROR!
//    }
    public static void main(String[] args) {
//        Cage<Monkey> cage = new Cage<Monkey>(new Monkey(), new Monkey());
//        Monkey monkey = cage.getAnimal1();

        Monkey monkey1  = new Monkey();
        Monkey monkey2 = new Monkey();
        System.out.println(Cage.isCompatible(monkey1, monkey2));

//        Cage<Lion> lionCage = new Cage<>();
//        lionCage.setAnimal1(new Lion());
    }
}
interface Eats {
    public void eat();
}
interface Runs {
    public void run();
}

class Cage<E extends Animal & Eats & Runs> {
    private E animal1;
    private E animal2;

    public Cage(E animal1, E animal2) {
        this.animal1 = animal1;
        this.animal2 = animal2;
    }

    public E getAnimal1() {
        return animal1;
    }

    public void setAnimal1(E animal1) {
        this.animal1 = animal1;
    }

    public E getAnimal2() {
        return animal2;
    }

    public void setAnimal2(E animal2) {
        this.animal2 = animal2;
    }

    public boolean isCompatible() {
        return animal1.getType().equals(animal2.getType());
    }

    public void feedAnimal() {
        animal1.eat();
        animal2.eat();
//        animal1.run();
    }

    public static <E extends Animal> boolean isCompatible(E animal1, E animal2) {
        return animal1.getType().equals(animal2.getType());
    }
}

class Animal{
    protected String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

class Lion extends Animal implements Eats, Runs {

    @Override
    public void eat() {

    }

    @Override
    public void run() {

    }
}

class Monkey extends Animal implements Eats, Runs {

    @Override
    public void eat() {

    }

    @Override
    public void run() {

    }
}
