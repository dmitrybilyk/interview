package com.conduct.interview.ksena;

public class Main {
    public static void main(String[] args) {
        int day = 30;
        int month = 12;
        int year = 2020;

        if (day > 31) {
            Date date = new Date();
        } else {

        }

        Date date = new Date(30, 4,2024);
        date.setDay(3334);

//        Object is being created here with help of constructor
        Date newDate = new Date(date);

        date.equals(newDate);
//        date.equals()
        System.out.println(date);
    }
}
