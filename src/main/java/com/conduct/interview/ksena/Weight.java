package com.conduct.interview.ksena;

public class Weight {
    private int kilos;
    private int grams;

    // Конструктор с двумя параметрами (килограммы и граммы)
    public Weight(int kilos, int grams) {
        if (kilos < 0 || grams < 0 || grams >= 1000) {
            this.kilos = 0;
            this.grams = 0;
        } else {
            this.kilos = kilos;
            this.grams = grams;
        }
    }

    // Конструктор копирования
    public Weight(Weight other) {
        this.kilos = other.kilos;
        this.grams = other.grams;
    }

    // Конструктор с одним параметром (общее количество граммов)
    public Weight(int totalGrams) {
        if (totalGrams < 0) {
            this.kilos = 0;
            this.grams = 0;
        } else {
            this.kilos = totalGrams / 1000;
            this.grams = totalGrams % 1000;
        }
    }

    // Геттер для kilos
    public int getKilos() {
        return kilos;
    }

    // Геттер для grams
    public int getGrams() {
        return grams;
    }

    // Метод equals для сравнения двух объектов Weight
    public boolean equals(Weight other) {
        return this.kilos == other.kilos && this.grams == other.grams;
    }

    // Метод lighter для проверки, меньше ли текущий вес
    public boolean lighter(Weight other) {
        if (this.kilos < other.kilos) {
            return true;
        } else if (this.kilos == other.kilos) {
            return this.grams < other.grams;
        }
        return false;
    }

    // Метод heavier для проверки, больше ли текущий вес
    public boolean heavier(Weight other) {
        if (this.kilos > other.kilos) {
            return true;
        } else if (this.kilos == other.kilos) {
            return this.grams > other.grams;
        }
        return false;
    }

    // Метод toString для форматирования веса в формате "kg.grams"
    public String toString() {
        return String.format("%d.%03d", kilos, grams);
    }

    // Метод add для добавления граммов и возврата нового объекта Weight
    public Weight add(int gramsToAdd) {
        int totalGrams = this.kilos * 1000 + this.grams + gramsToAdd;
        if (totalGrams < 0) {
            return new Weight(0, 0);
        }
        return new Weight(totalGrams);
    }
}
