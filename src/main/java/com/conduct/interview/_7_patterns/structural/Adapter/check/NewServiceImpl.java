package com.conduct.interview._7_patterns.structural.adapter.check;

import kotlin.NotImplementedError;

public class NewServiceImpl implements NewService {
    @Override
    public Long returnRate() {
        throw new NotImplementedError("Not Implemented");
    }
}
