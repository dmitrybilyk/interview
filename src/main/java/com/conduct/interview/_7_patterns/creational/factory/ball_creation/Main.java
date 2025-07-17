package com.conduct.interview._7_patterns.creational.factory.ball_creation;

import com.conduct.interview._7_patterns.creational.factory.ball_creation.factory.BallFactory;
import com.conduct.interview._7_patterns.creational.factory.ball_creation.model.Ball;
import com.conduct.interview._7_patterns.creational.factory.ball_creation.provider.BallFactoryProvider;

public class Main {
    public static void main(String[] args) {
        BallFactoryProvider ballFactoryProvider = BallFactoryProvider.getInstance();
        BallFactory ballFactory = ballFactoryProvider.getBallFactory();
        Ball ball = ballFactory.createBall();
        System.out.println(ball.getName());
    }
}
