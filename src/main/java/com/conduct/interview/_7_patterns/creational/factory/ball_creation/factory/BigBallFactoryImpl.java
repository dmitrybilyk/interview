package com.conduct.interview._7_patterns.creational.factory.ball_creation.factory;

import com.conduct.interview._7_patterns.creational.factory.ball_creation.model.Ball;
import com.conduct.interview._7_patterns.creational.factory.ball_creation.model.BigBall;

public class BigBallFactoryImpl implements BallFactory {
    @Override
    public Ball createBall() {
        return new BigBall("Big Ball");
    }
}
