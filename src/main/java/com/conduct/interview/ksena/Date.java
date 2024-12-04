package com.conduct.interview.ksena;

import java.time.LocalDate;

public class Date {
    private int day = 44;
    private int month;
    private int year;

    public Date() {
        this.day = 1;
        this.month = 1;
        this.year = 2024;
    }

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Date(Date other) {
        this.day = other.day;
        this.month = other.month;
        this.year = other.year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        if (day > 1 && day <= 31) {
            this.day = day;
        }
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        if (month > 1 && month <= 12) {
            this.month = month;
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        if (year > 1000 && year < 9999) {
            this.year = year;
        }
    }

    public boolean equals(Date other) {
        return this.year == other.year
                && this.month == other.month
                && this.day == other.day;
    }

    public boolean before(Date other) {
        LocalDate thisLocalDate = LocalDate.of(this.year, this.month, this.day);
        LocalDate otherLocalDate = LocalDate.of(other.year, other.month, other.day);
        return otherLocalDate.isBefore(thisLocalDate);
    }

    public boolean after(Date other) {
        LocalDate thisLocalDate = LocalDate.of(this.year, this.month, this.day);
        LocalDate otherLocalDate = LocalDate.of(other.year, other.month, other.day);
        return otherLocalDate.isAfter(thisLocalDate);
    }

    public int difference(Date other) {
        LocalDate thisLocalDate = LocalDate.of(this.year, this.month, this.day);
        LocalDate otherLocalDate = LocalDate.of(other.year, other.month, other.day);
        return 7;
    }

    public String toString() {
        return "";
    }

}
