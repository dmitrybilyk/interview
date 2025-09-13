package com.conduct.interview._1_bases.generics.udemy;

import java.util.ArrayList;
import java.util.List;

public class Check {
    public static void main(String[] args) {
        Comunity<Father> fatherComunity = new Comunity<>();
        fatherComunity.addMember(new Father());
        Comunity<Son> sonComunity = new Comunity<>();
        sonComunity.addMember(new Son());

        Comunity<FamilyMember> comunity = new Comunity<>();
        comunity.addMember(new Father());
        comunity.addMember(new Son());
        comunity.printAllMembers();

        List<Son> externalSons = List.of(new Son());
        comunity.printFriendsNames(externalSons);
        comunity.addMore(List.of(new Son()));
//        addMore(fatherComunity);

        ComunityUser comunityUser = new ComunityUser();
        comunityUser.addComunity(comunity);
    }

    private static void addMore(Comunity<Father> comunity) {
//        comunity.addMember(new Son());
    }
}

class Comunity<T extends FamilyMember> {
    private List<T> familyMembers = new ArrayList<>();

    public void addMember(T familyMember) {
        familyMembers.add(familyMember);
    }

    public void printAllMembers() {
        familyMembers.forEach(System.out::println);
    }

    public void organizePlayingGames() {
        familyMembers.forEach(FamilyMember::play);
    }

    public void printFriendsNames(List<? extends FamilyMember> familyMembers) {
        familyMembers.forEach(FamilyMember::play);
    }

    public void addMore(List<? super T> familyMembers) {
//        this.familyMembers.add(familyMembers.get(0))
        for (T member : this.familyMembers) {
            familyMembers.add(member); // ✅ we can add T into "? super T"
        }
    }
}

class ComunityUser {
    public void addComunity(Comunity<? super FamilyMember> comunity) {
        comunity.addMember(new Son());
        comunity.addMember(new Father());
    }
}

interface FamilyMember {
    public void play();
}

class Father implements FamilyMember {

    @Override
    public void play() {

    }
}

class Son implements FamilyMember {

    @Override
    public void play() {

    }
}
