package com.conduct.interview.ksena;

public class Date {
    private int day;
    private int month;
    private int year;

    public Date() {
        this(1, 1, 2024);
    }

    public Date(int day, int month, int year) {
        if (isValidDate(day, month, year)) {
            this.day = day;
            this.month = month;
            this.year = year;
        } else {
            throw new IllegalArgumentException("Invalid date");
        }
    }

    public Date(Date other) {
        this(other.day, other.month, other.year);
    }

    private boolean isValidDate(int day, int month, int year) {
        if (month < 1 || month > 12 || day < 1) return false;
        int[] daysInMonth = {31, isLeapYear(year) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        return day <= daysInMonth[month - 1];
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        if (isValidDate(day, this.month, this.year)) {
            this.day = day;
        }
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        if (isValidDate(this.day, month, this.year)) {
            this.month = month;
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        if (isValidDate(this.day, this.month, year)) {
            this.year = year;
        }
    }

    public boolean equals(Date other) {
        return this.year == other.year && this.month == other.month && this.day == other.day;
    }

    public boolean before(Date other) {
        if (this.year != other.year) return this.year < other.year;
        if (this.month != other.month) return this.month < other.month;
        return this.day < other.day;
    }

    public boolean after(Date other) {
        return !before(other) && !equals(other);
    }

    public int difference(Date other) {
        return Math.abs(this.calculateDate(this.year, this.month, this.day)
                - other.calculateDate(other.year, other.month, other.day));
    }

    public String toString() {
        return String.format("%02d/%02d/%04d", this.month, this.day, this.year);
    }

    public Date tomorrow() {
        if (isValidDate(this.day + 1, this.month, this.year)) {
            return new Date(this.day + 1, this.month, this.year);
        } else if (isValidDate(1, this.month + 1, this.year)) {
            return new Date(1, this.month + 1, this.year);
        } else if (this.year < 9999) {
            return new Date(1, 1, this.year + 1);
        }
        return this; // No "tomorrow" beyond 9999.
    }

    public int calculateDate(int year, int month, int day) {
        if (month < 3) {
            year--;
            month += 12;
        }
        return 365 * year + year / 4 - year / 100 + year / 400 + (153 * (month - 3) + 2) / 5 + day;
    }

    public boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
