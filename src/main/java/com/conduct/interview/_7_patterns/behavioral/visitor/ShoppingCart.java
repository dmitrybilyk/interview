package com.conduct.interview._7_patterns.behavioral.visitor;

// Visitor interface
interface Visitor {
    void visit(Book book);
    void visit(Fruit fruit);
}

// Concrete Visitor
class PriceCalculator implements Visitor {
    @Override
    public void visit(Book book) {
        System.out.println("Book price: " + book.getPrice());
    }

    @Override
    public void visit(Fruit fruit) {
        System.out.println("Fruit price: " + (fruit.getWeight() * fruit.getPricePerKg()));
    }
}

// Element interface
interface ItemElement {
    void accept(Visitor visitor);
}

// Concrete Element (Book)
class Book implements ItemElement {
    private double price;
    
    public Book(double price) {
        this.price = price;
    }
    
    public double getPrice() {
        return price;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

// Concrete Element (Fruit)
class Fruit implements ItemElement {
    private double pricePerKg;
    private double weight;

    public Fruit(double pricePerKg, double weight) {
        this.pricePerKg = pricePerKg;
        this.weight = weight;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}

// Client
public class ShoppingCart {
    public static void main(String[] args) {
        ItemElement[] items = new ItemElement[] {
            new Book(20), 
            new Fruit(5, 2)
        };

        Visitor priceCalculator = new PriceCalculator();
        for (ItemElement item : items) {
            item.accept(priceCalculator);
        }
    }
}
