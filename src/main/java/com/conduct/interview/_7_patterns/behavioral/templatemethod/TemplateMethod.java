package com.conduct.interview._7_patterns.behavioral.templatemethod;

public class TemplateMethod {
	public static void main(String[] args) {
		BuildingHouse buildingHouse = new BuildingTheCheapHouse();
		buildingHouse.buildingBasement();
		buildingHouse.buildTheFloor();
		buildingHouse.buildTheBox();
	}
}

abstract class BuildingHouse {
	void buildingBasement() {
		System.out.println("Building the basement");
	}
	abstract void buildTheFloor();
	abstract void buildTheBox();
}

class BuildingTheCheapHouse extends BuildingHouse {
	public void buildTheFloor() {
		System.out.println("Building the cheap floor");
	}
	public void buildTheBox() {
		System.out.println("Building the cheap box");
	}
}

class BuildingTheLuxuryHouse extends BuildingHouse {
	public void buildTheFloor() {
		System.out.println("Building the luxury floor");
	}
	public void buildTheBox() {
		System.out.println("Building the luxury box");
	}
}