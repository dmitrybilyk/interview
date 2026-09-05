package com.conduct.interview._3_spring._14_object_mapper;

public class User {

    private String userName;
    private int accountAge;

    public User() {
        // Jackson needs a no-arg constructor for deserialization (or a @JsonCreator)
    }

    public User(String userName, int accountAge) {
        this.userName = userName;
        this.accountAge = accountAge;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAccountAge() {
        return accountAge;
    }

    public void setAccountAge(int accountAge) {
        this.accountAge = accountAge;
    }

    @Override
    public String toString() {
        return "User{userName='" + userName + "', accountAge=" + accountAge + "}";
    }
}
