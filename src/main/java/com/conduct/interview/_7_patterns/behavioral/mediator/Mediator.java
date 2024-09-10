package com.conduct.interview._7_patterns.behavioral.mediator;

import java.util.List;
import java.util.ArrayList;


public class Mediator {
	public static void main(String[] args) {
		ChatMediator mediator = new MediatorImpl();
		User dmytro = new UserImpl("Dmytro", mediator);
		User andry = new UserImpl("Andry", mediator);
		User mykyta = new UserImpl("Mykyta", mediator);
		mediator.addUser(dmytro);
		mediator.addUser(andry);
		mediator.addUser(mykyta);
		mykyta.sendMessage("hello to all");

	}
}

abstract class User {
	abstract void sendMessage(String message);
	abstract void receiveMessage(String message);
}

class UserImpl extends User {
	private String name;
	private ChatMediator mediator;
	public UserImpl(String name, ChatMediator mediator) {
		this.name = name;
		this.mediator = mediator;
	}	
	public void sendMessage(String message) {
		mediator.sendMessage(this, message);
	}
	public void receiveMessage(String message) {
		System.out.println("User with name " + name + "  received the message - " + message);
	}
}

abstract class ChatMediator {
	abstract void sendMessage(User user, String message);
	abstract void addUser(User user);
}

class MediatorImpl extends ChatMediator {
	private List<User> users;
	public MediatorImpl() {
		users = new ArrayList<>();
	}
	
	public void sendMessage(User sentUser, String message) {
		for(User user: users) {
			if (!user.equals(sentUser)) {
				user.receiveMessage(message);
			}
		}
	}

	public void addUser(User user) {
		users.add(user);
	}

}