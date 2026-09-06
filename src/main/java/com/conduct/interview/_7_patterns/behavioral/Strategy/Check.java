package com.conduct.interview._7_patterns.behavioral.strategy;

public class Check {
    public static void main(String[] args) {
        BankingTransaction bankingTransaction = new BankingTransaction(new CardPayment());
        bankingTransaction.pay(20);
    }
}

class BankingTransaction {
    private Payment strategy;
    public BankingTransaction(Payment strategy) {
        this.strategy = strategy;
    }

    public void pay(int amount) {
        System.out.println("Paying " + amount + strategy);
        strategy.pay(amount);
    }
}

interface Payment {
    void pay(int amount);
}

class CashPayment implements Payment {
    @Override
    public void pay(int amount) {
        System.out.println("in CashPayment");
    }
}

class CardPayment implements Payment {
    @Override
    public void pay(int amount) {
        System.out.println("in Card Payment");
    }
}
