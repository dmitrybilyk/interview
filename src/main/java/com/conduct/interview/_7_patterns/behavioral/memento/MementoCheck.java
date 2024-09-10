package com.conduct.interview._7_patterns.behavioral.memento;

public class MementoCheck {
	public static void main(String[] args) {
		Car car = new Car("car1", 100);
		System.out.println(car);
		CarCareTaker carCareTaker = new CarCareTaker();
		carCareTaker.save(car);
		car.setName("car2");
		car.setSpeed(200);
		System.out.println(car);
		carCareTaker.restore(car);
		System.out.println(car);
	}
}


class CarCareTaker {
	private Object memento;
	public void save(Car car) {
		memento = car.save();
	}

	public void restore(Car car) {
		car.restore(memento);
	}
}


class Car {
	private String name;
	private int speed;
	public Car(String name, int speed) {
		this.name = name;
		this.speed = speed;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public Memento save() {
		return new Memento(this.name, this.speed);
	}

	public void restore(Object mementoObj) {
		Memento memento = (Memento) mementoObj;
		this.speed = memento.speed;
		this.name = memento.name;
	}

	public class Memento {
		private String name;
		private int speed;
		public Memento(String name, int speed) {
			this.name = name;
			this.speed = speed;
		}

	}

	public String toString() {
		return "Car is " + name + " speed is " + speed;
	}
}