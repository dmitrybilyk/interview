//package com.conduct.interview._7_patterns.behavioral.state;
//
//public class Check {
//    public static void main(String[] args) {
//        Gym gym = new Gym();
//        gym.doSomething();
//
//        gym.changePosition(new MyPositionDown());
//    }
//}
//
//
//class Gym {
//    private Position positionUp;
//    private Position positionDown;
//    private Position positionMiddle;
//    private Position currentPosition;
//    public Gym() {
//        this.positionUp = new MyPositionUp();
//        this.positionDown = new MyPositionDown();
//        this.positionMiddle = new MyPositionMiddle();
//        this.currentPosition = positionMiddle;
//    }
//
//    public void changePosition() {
//        this.currentPosition = positionDown;
//    }
//
//    public void doSomething() {
//        this.currentPosition.doOnPosition();
//    }
//}
//
//interface Position {
//    void doOnPosition();
//}
//
//class MyPositionUp implements Position {
//    @Override
//    public void doOnPosition() {
//        System.out.println("In Up position");
//    }
//}
//
//class MyPositionDown implements Position {
//    @Override
//    public void doOnPosition() {
//        System.out.println("In Down position");
//    }
//}
//
//class MyPositionMiddle implements Position {
//    @Override
//    public void doOnPosition() {
//        System.out.println("In Down position");
//    }
//}
