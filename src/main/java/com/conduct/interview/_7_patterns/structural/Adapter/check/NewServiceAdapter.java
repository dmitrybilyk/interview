package com.conduct.interview._7_patterns.structural.adapter.check;

public class NewServiceAdapter implements NewService {
    private final LegacyService legacyService;

    public NewServiceAdapter(LegacyService legacyService) {
        this.legacyService = legacyService;
    }

    @Override
    public Long returnRate() {
        return legacyService.returnRate().longValue();
    }
}
