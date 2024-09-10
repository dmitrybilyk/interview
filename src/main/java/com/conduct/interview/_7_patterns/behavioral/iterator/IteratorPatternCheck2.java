package com.conduct.interview._7_patterns.behavioral.iterator;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IteratorPatternCheck2 {
    public static void main(String[] args) {

    }
}

@Getter
@Setter
class FootballPlayer {
    private String name;
}

interface PlayerCollection {
    FootballPlayersIterator createFootballPlayerIterator();
}

class FootballPlayerCollection implements PlayerCollection {
//    FootballPlayersIterator createFootballPlayerIterator;

    List<FootballPlayer> footballPlayers = new ArrayList<>();

    public void addFootballPlayer(FootballPlayer footballPlayer) {
        footballPlayers.add(footballPlayer);
    }

    public List<FootballPlayer> getTopFootballPlayers() {
        return footballPlayers.stream().filter(footballPlayer ->
                footballPlayer.getName().startsWith("top")).collect(Collectors.toList());
    }

    @Override
    public FootballPlayersIterator createFootballPlayerIterator() {
        return null;
    }
}

interface FootballPlayersIterator {
    boolean hasNext();
    FootballPlayer next();
}

class FootballPlayersIteratorImpl implements FootballPlayersIterator {

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public FootballPlayer next() {
        return null;
    }
}
