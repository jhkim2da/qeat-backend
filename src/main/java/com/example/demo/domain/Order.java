package com.example.demo.domain;

import com.example.demo.dto.order.OrderCreateRequest;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booth_id")
    private Long boothId;

    @Column(name = "table_id")
    private Long tableId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "total_price")
    private int totalPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getBoothId() {
        return boothId;
    }

    public Long getTableId() {
        return tableId;
    }

    public Status getStatus() {
        return status;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    private Order(Long boothId, Long tableId, int totalPrice) {
        this.boothId = boothId;
        this.tableId = tableId;
        this.totalPrice = totalPrice;
        this.status = Status.CHECK;
    }

    public static Order create(Long boothId, Long tableId, int totalPrice) {
        return new Order(boothId, tableId, totalPrice);
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    protected Order() {}
}
