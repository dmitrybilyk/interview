package com.conduct.interview._7_patterns.structural.adapter.check;

import java.util.Random;

public class LegacyServiceImpl implements LegacyService {
    @Override
    public Integer returnRate() {
        return new Random(3).nextInt(100);
    }
}
