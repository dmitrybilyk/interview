package com.conduct.interview._5_solid._3_liskov_principle;

public class LiskovPrincipleCheck {
    public static void main(String[] args) {

    }
}


class JustBird {
    void fly() {

    }
}

class Hawk extends JustBird {
    @Override
    void fly() {
        System.out.println("I'm flying well");
    }
}

class Chicken extends Bird {
    @Override
    public void fly() {
        System.out.println("but I can't fly");
    }
}

// to fix we should break hierarchi. We can create Bird, and FlyableBird and
// Chicken will extend Bird. But Hawk will extend FlyableBird

//And another case we will fix with principle 'tell, don't ask'
// where we should avoid instanceOf
