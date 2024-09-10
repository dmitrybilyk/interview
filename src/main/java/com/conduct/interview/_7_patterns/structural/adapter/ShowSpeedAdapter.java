package com.conduct.interview._7_patterns.structural.adapter;

public class ShowSpeedAdapter {
	public static void main(String[] args) {
        MilesMovable bmw = new Bmw();
        KmMovableAdapter bmwAdapter = new KmMovableAdapterImpl(bmw);
        System.out.println(bmwAdapter.getSpeedInKm());
	}
}

interface MilesMovable {
    // returns speed in MPH 
    double getSpeedInMiles();
}

class Bmw implements MilesMovable {
 
    @Override
    public double getSpeedInMiles() {
        return 268;
    }
}

interface KmMovableAdapter {
    // returns speed in KMPH 
    double getSpeedInKm();
}

class KmMovableAdapterImpl implements KmMovableAdapter {
    private MilesMovable luxuryCars;
    
    public KmMovableAdapterImpl(MilesMovable milesMovable) {
        this.luxuryCars = milesMovable;
    }
    @Override
    public double getSpeedInKm() {
        return convertMPHtoKMPH(luxuryCars.getSpeedInMiles());
    }
    
    private double convertMPHtoKMPH(double mph) {
        return mph * 1.60934;
    }
}