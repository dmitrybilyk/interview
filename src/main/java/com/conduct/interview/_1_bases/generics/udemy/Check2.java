//package com.conduct.interview._1_bases.generics.udemy;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Check2 {
//    public static void main(String[] args) {
////        Team team = new Team();
////        Player player = new Player();
////        Forward forward = new Forward();
////        Player defender = new Defender();
////        team.addPlayer(player);
////        team.addPlayer(forward);
////        team.checkAll();
//
//        Team<Forward> forwardTeam = new Team<>();
//        forwardTeam.addPlayer(new Forward());
//        forwardTeam.addPlayer(new Forward());
//        Team<Defender> defenderTeam = new Team<>();
//        defenderTeam.addPlayer(new Defender());
//        defenderTeam.addPlayer(new Defender());
//        Team<Player> playerTeam = new Team<>();
//        playerTeam.addPlayer(new Defender());
//        playerTeam.addPlayer(new Defender());
//
//        Stadium stadium = new Stadium();
//        stadium.startSession(playerTeam);
//        stadium.getFromTeam(forwardTeam);
//
//        stadium.checkWithDoctor(defenderTeam);
//    }
//}
//
//class Stadium {
//
//    public void startSession(Team<? super Player> team) {
//        team.addPlayer(new Player());
////        team.getPlayerList().forEach(player -> player.doTrainingSession());
//    }
//
//    public void getFromTeam(Team<? super Forward> team) {
//        System.out.println(team.getPlayerList());
//    }
//
//    public void checkWithDoctor(Team<? extends Healable> forwardTeam) {
//        forwardTeam.getPlayerList().forEach(Healable::heal);
//    }
//}
//
//class Team<T> {
//    private List<T> playerList = new ArrayList<>();
//
//    public List<T> getPlayerList() {
//        return playerList;
//    }
//
//    public void addPlayer(T player) {
//        playerList.add(player);
//    }
//
//    public void train(T player) {
//
//    }
//
//    public T checkFirst() {
//        return playerList.getFirst();
//    }
//
//    public void checkAll() {
//        playerList.forEach(player -> System.out.println("checking " + player));
//    }
//}
//
//class Player {
//    public void doTrainingSession() {
//        System.out.println("Doing session for " + this);
//    }
//}
//
//interface Healable {
//    void heal();
//}
//
//class Forward extends Player {
//
//}
//
//class Defender extends Player implements Healable {
//
//    @Override
//    public void heal() {
//
//    }
//}