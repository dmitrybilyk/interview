package com.conduct.interview._7_patterns.behavioral.chain;

public class ChainCheck {
	public static void main(String[] args) {
		AuthenticationLayer authenticationLayer1 = new AuthenticationLayer1();
		AuthenticationLayer authenticationLayer2 = new AuthenticationLayer2();
		AuthenticationLayer authenticationLayer3 = new AuthenticationLayer3();
		authenticationLayer1.setNext(authenticationLayer2);
		authenticationLayer2.setNext(authenticationLayer3);
		authenticationLayer1.login("name", "password");
	}
}

abstract class AuthenticationLayer {
	AuthenticationLayer next;
	void setNext(AuthenticationLayer authenticationLayer) {
		this.next = authenticationLayer;
	}
	abstract boolean login(String name, String password);
}

class AuthenticationLayer1 extends AuthenticationLayer {
	public boolean login(String name, String password) {
		if (next != null && name != null) {
			System.out.println("Successfull 1");
			return next.login(name, password);
		}
		return false;
	}
}

class AuthenticationLayer2 extends AuthenticationLayer {
	public boolean login(String name, String password) {
		if (next != null && name != null) {
			System.out.println("Successfull 2");
			return next.login(name, password);
		}
		return false;
	}
}

class AuthenticationLayer3 extends AuthenticationLayer {
	public boolean login(String name, String password) {
		if (name != null) {
			System.out.println("Successfull 3");
			if (next != null) {
				return next.login(name, password);
			}			
		}
		return false;
	}
}