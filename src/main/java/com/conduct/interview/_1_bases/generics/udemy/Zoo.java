package com.conduct.interview._1_bases.generics.udemy;

public class Zoo {
    public static void main(String[] args) {
        Cage<Monkey> cage = new Cage<Monkey>(new Monkey(), new Monkey());
        Monkey monkey = cage.getAnimal1();

//        Cage<Lion> lionCage = new Cage<>();
//        lionCage.setAnimal1(new Lion());
    }
}

class Cage<E> {
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
}

class Animal{

}

class Lion extends Animal {

}

class Monkey extends Animal {

}
