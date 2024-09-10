package com.conduct.interview._7_patterns.structural.adapter;

public class AdapterCheck2 {
	public static void main(String[] args) {
		Charger charger = new USCharger();
		ChargeAdapter chargerAdapter = new ChargerAdapterImpl(charger);
		chargerAdapter.chargeWith220V();
	}
}

interface Charger {
	void chargeWith120V();
}

class USCharger implements Charger {
	public void chargeWith120V() {
		System.out.println("Charging with 120 volt");
	}
}

interface ChargeAdapter {
	void chargeWith220V();
}

class ChargerAdapterImpl implements ChargeAdapter {
	private Charger usCharger;
	public ChargerAdapterImpl(Charger usCharger) {
		this.usCharger = usCharger;
	}
	public void chargeWith220V() {
		usCharger.chargeWith120V();
		System.out.println("Adding additional 100 Volts");
	}
}

