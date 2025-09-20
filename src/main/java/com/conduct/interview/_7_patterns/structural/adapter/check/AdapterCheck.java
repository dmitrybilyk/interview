package com.conduct.interview._7_patterns.structural.adapter.check;

import java.math.BigInteger;

public class AdapterCheck {
    public static void main(String[] args) {
        ModernCalculationServiceAdapter modernCalculationServiceAdapter = new ModernCalculationServiceAdapter(new ModernCalculation());
        CalculationClient calculationClient = new CalculationClient(modernCalculationServiceAdapter);
        calculationClient.initiateCalculation();

        ModernCalculationClient modernCalculationClient = new ModernCalculationClient(
                new LegacyCalculationClientAdapter(new CalculateServiceImpl()));
        modernCalculationClient.useModernCalculationService(BigInteger.TEN);

    }
}

class LegacyCalculationClientAdapter extends ModernCalculation {
    public LegacyCalculationClientAdapter(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    private CalculationService calculationService;

    @Override
    BigInteger calculateEffectively(BigInteger param) {
        return BigInteger.valueOf(calculationService.calculate(Integer.parseInt(param.toString())));
    }
}

class ModernCalculationServiceAdapter implements CalculationService {
    private ModernCalculation modernCalculation;

    public ModernCalculationServiceAdapter(ModernCalculation modernCalculation) {
        this.modernCalculation = modernCalculation;
    }

    @Override
    public int calculate(int parameter) {
        return Integer.parseInt(modernCalculation.calculateEffectively(BigInteger.valueOf(parameter)).toString());
    }
}

class CalculationClient {
    private CalculationService calculationService;

    public CalculationClient(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    void initiateCalculation() {
        System.out.println(calculationService.calculate(10));
    }
}

class ModernCalculationClient {
    private ModernCalculation modernCalculation;

    public ModernCalculationClient(ModernCalculation modernCalculation) {
        this.modernCalculation = modernCalculation;
    }

    BigInteger useModernCalculationService(BigInteger param) {
        return modernCalculation.calculateEffectively(param);
    }
}

interface CalculationService {
    int calculate(int parameter);
}

class CalculateServiceImpl implements CalculationService {

    @Override
    public int calculate(int parameter) {
        return parameter * 10;
    }
}

class ModernCalculation {
    BigInteger calculateEffectively(BigInteger param) {
        return param.multiply(BigInteger.TEN).multiply(BigInteger.TEN);
    }
}