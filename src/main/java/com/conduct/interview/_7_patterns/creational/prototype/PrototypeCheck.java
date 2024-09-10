package com.conduct.interview._7_patterns.creational.prototype;

import java.util.HashMap;
import java.util.Map;

public class PrototypeCheck {
    public static void main(String[] args) {
        GoodUser user = new GoodUser();
        user.name = "name";
        user.goodName = "good name";

        GoodUser userPrototyped = (GoodUser) user.clone();
        System.out.println(userPrototyped);

        PrototypeRegistryCheck prototypeRegistryCheck = new PrototypeRegistryCheck();
        prototypeRegistryCheck.getByName("name1");

    }
}

abstract class User {
    protected String name;

    public User(User user) {
        this.name = user.name;
    }

    protected User() {
    }

    protected abstract User clone();
}

class GoodUser extends User {
    protected String goodName;

    @Override
    public String toString() {
        return "GoodUser{" +
                "name='" + name + '\'' +
                ", goodName='" + goodName + '\'' +
                '}';
    }

    public GoodUser(GoodUser user) {
        super(user);
        this.goodName = user.goodName;
    }

    public GoodUser() {
    }

    @Override
    protected User clone() {
        return new GoodUser(this);
    }
}

class BadUser extends User {
    protected String badName;

    public BadUser(BadUser user) {
        super(user);
        this.badName = user.badName;
    }

    public BadUser() {

    }

    @Override
    protected User clone() {
        return new BadUser(this);
    }
}

class PrototypeRegistryCheck {
    public Map<String, User> userMap = new HashMap<>();

    public PrototypeRegistryCheck() {
        GoodUser goodUser = new GoodUser();
        goodUser.name = "name1";
        goodUser.goodName = "goodName1";
        BadUser badUser = new BadUser();
        badUser.name = "name2";
        badUser.badName = "badName2";
        put(badUser);
    }

    public User getByName(String name) {
        return userMap.get(name);
    }

    public void put(User user) {
        userMap.put(user.name, user);
    }
}

