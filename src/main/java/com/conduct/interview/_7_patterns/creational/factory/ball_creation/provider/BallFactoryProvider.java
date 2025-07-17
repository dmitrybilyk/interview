package com.conduct.interview._7_patterns.creational.factory.ball_creation.provider;

import com.conduct.interview._7_patterns.creational.factory.ball_creation.factory.BallFactory;
import com.conduct.interview._7_patterns.creational.factory.ball_creation.factory.BigBallFactoryImpl;
import com.conduct.interview._7_patterns.creational.factory.ball_creation.factory.MiddleSizeBallFactoryImpl;
import com.conduct.interview._7_patterns.creational.factory.ball_creation.factory.SmallBallFactoryImpl;

public class BallFactoryProvider {
    private final BallFactory ballFactory;
    public BallFactory getBallFactory() {
        return ballFactory;
    }

    private BallFactoryProvider() {
        boolean bigBall = Boolean.getBoolean("big.ball");
        boolean specialCase = Boolean.getBoolean("special.case");
        if (specialCase) {
            ballFactory = new MiddleSizeBallFactoryImpl();
        } else if (bigBall) {
            ballFactory = new BigBallFactoryImpl();
        } else {
            ballFactory = new SmallBallFactoryImpl();
        }

    }

    private static class SingletonHolder {
        private final static BallFactoryProvider INSTANCE = new BallFactoryProvider();
    }

    public static BallFactoryProvider getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
