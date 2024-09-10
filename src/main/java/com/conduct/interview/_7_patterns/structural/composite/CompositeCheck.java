package com.conduct.interview._7_patterns.structural.composite;

import java.util.List;
import java.util.ArrayList;

public class CompositeCheck {
	public static void main(String[] args) {
		Participant human1 = new HumanParticipant("Dmytro");
		Participant humanChild1 = new HumanParticipant("Mykyta");
		Participant horse1 = new HorseParticipant();
		HumansTeam team = new HumansTeam();
		HumansTeam childTeam = new HumansTeam();
		childTeam.add(humanChild1);
		team.add(human1);
		team.add(horse1);
		team.add(childTeam);
		team.takePart("Who eats the most");
	}
}

interface Participant {
	void takePart(String competitionName);
}

class HumanParticipant implements Participant {
	private String name;
	public HumanParticipant(String name) {
		this.name = name;
	}
	public void takePart(String competitionName) {
		System.out.println(name + " is participaiting in " + competitionName);
	}
}
class HorseParticipant implements Participant {
	public void takePart(String competitionName) {
		System.out.println("Horse is participaiting in " + competitionName);
	}
}

class HumansTeam implements Participant {
	private List<Participant> participants = new ArrayList<>();
	public void takePart(String competitionName) {
		for(Participant participant: participants) {
			participant.takePart(competitionName);
		}
	}
	public void add(Participant participant) {
		participants.add(participant);
	}
}
