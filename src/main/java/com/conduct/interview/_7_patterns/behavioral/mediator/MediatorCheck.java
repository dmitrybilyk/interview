package com.conduct.interview._7_patterns.behavioral.mediator;

import java.util.List;
import java.util.ArrayList;

public class MediatorCheck {
	public static void main(String[] args) {
		Dispatcher dispatcher = new InternetDispatcherImpl();
		Plane plane1 = new TurboPlaneImpl("plane1", dispatcher);
		Plane plane2 = new TurboPlaneImpl("plane2", dispatcher);
		Plane plane3 = new TurboPlaneImpl("plane3", dispatcher);
		dispatcher.addPlane(plane1);
		dispatcher.addPlane(plane2);
		dispatcher.addPlane(plane3);
		plane2.landing();
	}
}

abstract class Plane {
	protected String name;
	public Plane(String name) {
		this.name = name;
	}
	abstract void landing();
	abstract void waiting();
}

class PlaneImpl extends Plane {
	private Dispatcher dispatcher;
	public PlaneImpl(String name, Dispatcher dispatcher) {
		super(name);
		this.dispatcher = dispatcher;
	}
	public void landing() {
		dispatcher.landing(this);
	}
	public void waiting() {
		System.out.println("The plane " + name + " will wait for other to land");
	}
}

class TurboPlaneImpl extends Plane {
	private Dispatcher dispatcher;
	public TurboPlaneImpl(String name, Dispatcher dispatcher) {
		super(name);
		this.dispatcher = dispatcher;
	}
	public void landing() {
		System.out.println("Turbo plane is" + name + " is landing");
		dispatcher.landing(this);
	}
	public void waiting() {
		System.out.println("The Turbo plane " + name + " will wait for other to land");
	}
}

abstract class Dispatcher {
	abstract void landing(Plane plane);
	abstract void addPlane(Plane plane);
}

class DispatcherImpl extends Dispatcher {
	private List<Plane> planes;
	public DispatcherImpl() {
		planes = new ArrayList<>();
	}
	public void addPlane(Plane plane) {
		planes.add(plane);
	}
	public void landing(Plane plane) {
		for(Plane planeToWait: planes) {
			if (!plane.equals(planeToWait)) {
				planeToWait.waiting();
			}
		}
	}
}

class InternetDispatcherImpl extends Dispatcher {
	private List<Plane> planes;
	public InternetDispatcherImpl() {
		planes = new ArrayList<>();
	}
	public void addPlane(Plane plane) {
		planes.add(plane);
	}
	public void landing(Plane plane) {
		for(Plane planeToWait: planes) {
			if (!plane.equals(planeToWait)) {
				System.out.println("Managed by the internet");
				planeToWait.waiting();
			}
		}
	}
}