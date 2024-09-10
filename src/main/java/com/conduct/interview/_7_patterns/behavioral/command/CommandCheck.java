package com.conduct.interview._7_patterns.behavioral.command;

public class CommandCheck {
	public static void main(String[] args) {
		CafeCook cafeCook = new CafeNonSugarCakeCook();
		CafeCommand cafeCommand = new ProduceCakeCommand(cafeCook);
		CafeCustomer cafeCustomer = new CafeCustomer(cafeCommand);
		cafeCustomer.makeOrder();
	}

}

interface CafeCommand {
	void execute();
}

class ProduceCakeCommand implements CafeCommand {
	private CafeCook cafeCook;
	public ProduceCakeCommand(CafeCook cafeCook) {
		this.cafeCook = cafeCook;
	}
	public void execute() {
		cafeCook.cook();
	}
}


class CafeCustomer {
	private CafeCommand cafeCommand;
	public CafeCustomer(CafeCommand cafeCommand) {
		this.cafeCommand = cafeCommand;
	}
	public void makeOrder() {
		cafeCommand.execute();
	}
}

interface CafeCook {
	void cook();
}

class CafeCakeCook implements CafeCook {
	public void cook() {
		System.out.println("cooking the cake");
	}
}

class CafeNonSugarCakeCook implements CafeCook {
	public void cook() {
		System.out.println("cooking the cake without sugar");
	}
}


class CafeSugarCakeCook implements CafeCook {
	public void cook() {
		System.out.println("cooking the cake with sugar");
	}
}

class CafeSoupCook implements CafeCook {
	public void cook() {
		System.out.println("cooking the soup");
	}
}

