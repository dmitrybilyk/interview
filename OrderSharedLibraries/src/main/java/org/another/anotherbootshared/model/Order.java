package org.another.anotherbootshared.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document("orders")
public class Order {
    @Id
    private String id;
    private double amount;
    private String status; // PENDING, PAID, FAILED


    public Order() {}


    public Order(String id, double amount, String status) {
        this.id = id;
        this.amount = amount;
        this.status = status;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }


    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}