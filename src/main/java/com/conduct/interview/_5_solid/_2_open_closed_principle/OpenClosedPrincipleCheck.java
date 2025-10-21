package com.conduct.interview._5_solid._2_open_closed_principle;

public class OpenClosedPrincipleCheck {
    public static void main(String[] args) {

    }
}

class DeliveryService {
    int calculateCostViolates(String condition) {
        int basePrice = 100;
        if (condition.contains("manual")) {
            basePrice += 20;
        } else if (condition.contains("auto")) {
            basePrice += 10;
        }
        return basePrice;
    }
    int calculateCostProper(DeliveryMethod deliveryMethod) {
        int basePrice = 100;
        basePrice += deliveryMethod.getAdditionalCost();
        return basePrice;
    }
}

interface DeliveryMethod {
    int getAdditionalCost();
}

class ManualMethod implements DeliveryMethod {

    @Override
    public int getAdditionalCost() {
        return 20;
    }
}

class AutoMethod implements DeliveryMethod {

    @Override
    public int getAdditionalCost() {
        return 10;
    }
}


