package com.conduct.interview._7_patterns.behavioral.command;

public class CommandCheck2 {
	public static void main(String[] args) {
		HouseBuilder houseBuilder = new EconomHouseBuilder();
		BuildHouseCommand buildHouseCommand = new BuildCityHouseCommand(houseBuilder, "house city", 100);
		HouseCustomer houseCustomer = new HouseCustomer(buildHouseCommand);
		houseCustomer.orderTheHouse();
		
	}
}


class HouseCustomer {
	private BuildHouseCommand houseCommand;
	public HouseCustomer(BuildHouseCommand buildHouseCommand) {
		this.houseCommand = buildHouseCommand;
	}
	public void orderTheHouse() {
		houseCommand.execute();
	}

}

abstract class BuildHouseCommand {
	protected String name;
	protected int size;
	public BuildHouseCommand(String name, int size) {
		this.name = name;
		this.size = size;
	}
	abstract void execute();
}

class BuildCityHouseCommand extends BuildHouseCommand {
	private HouseBuilder houseBuilder;
	public BuildCityHouseCommand(HouseBuilder houseBuilder, String name, int size) {
		super(name, size);
		this.houseBuilder = houseBuilder;
	}
	public void execute() {
		houseBuilder.buildHouse(name, size);
	}
}

class BuildCountryHouseCommand extends BuildHouseCommand {
	private HouseBuilder houseBuilder;
	public BuildCountryHouseCommand(HouseBuilder houseBuilder, String name, int size) {
		super(name, size);
		this.houseBuilder = houseBuilder;
	}
	public void execute() {
		houseBuilder.buildHouse(name, size);
	}
}



interface HouseBuilder {
	void buildHouse(String name, int size);
}

class LuxuryHouseBuilder implements HouseBuilder {
	public void buildHouse(String name, int size) {
		System.out.println(" Building the luxury house with name " + name + " and size " + size);
	}
}

class EconomHouseBuilder implements HouseBuilder {
	public void buildHouse(String name, int size) {
		System.out.println(" Building the econom house with name " + name + " and size " + size);
	}
}

