package org.another.orderdeliveryservice;

import java.time.LocalDateTime;

public class Delivery {
    private String orderId;
    private LocalDateTime createdAt;
    private String status;

    public Delivery() {
    }

    public Delivery(String orderId, LocalDateTime createdAt, String status) {
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "orderId='" + orderId + '\'' +
                ", createdAt=" + createdAt +
                ", status='" + status + '\'' +
                '}';
    }
}