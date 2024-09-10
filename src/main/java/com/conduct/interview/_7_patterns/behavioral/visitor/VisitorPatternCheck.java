package com.conduct.interview._7_patterns.behavioral.visitor;

public class VisitorPatternCheck {
	public static void main(String[] args) {
		QuickCar quickCar = new QuickCar();
		quickCar.name = "quick car";
		quickCar.messageOfQuickCar = "- quick message";
		quickCar.speed = 100;

		VisitorPat visitor = new NameChangerVisitor();
		quickCar.accept(visitor);
		System.out.println(quickCar);
		visitor = new ImproveSpeedVisitor();
		quickCar.accept(visitor);
		System.out.println(quickCar);
//		int p1 = 'N';
//		int p2 = p1 + 32;
//		System.out.println((char) p2);

	}
}

abstract class Car {
	String name;
	int speed;
	abstract void accept(VisitorPat visitor);
}

class QuickCar extends Car {
	public String messageOfQuickCar;
	void printMessageOfQuickCar() {
		System.out.println(messageOfQuickCar);
	}
	void accept(VisitorPat visitor) {
		visitor.visitQuickCar(this);
	}

	@Override
	public String toString() {
		return "name = " + this.name + "; speed = " + this.speed;
	}
}

class SlowCar extends Car {
	public String messageOfSlowCar;
	void printMessageOfSlowCar() {
		System.out.println(messageOfSlowCar);
	}
	void accept(VisitorPat visitor) {
		visitor.visitSlowCar(this);
	}
}

interface VisitorPat {
	void visitQuickCar(QuickCar car);
	void visitSlowCar(SlowCar car);
}

class ImproveSpeedVisitor implements VisitorPat {
	public void visitQuickCar(QuickCar car) {
		car.speed += 20;
	}
	public void visitSlowCar(SlowCar car) {
		car.speed += 20;
	}
}


class NameChangerVisitor implements VisitorPat {
	public void visitQuickCar(QuickCar car) {
		car.name += car.messageOfQuickCar + "-changed";
	}
	public void visitSlowCar(SlowCar car) {
		car.name += car.messageOfSlowCar + "-changed";
	}
}