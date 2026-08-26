package com.qeat.domain;

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

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void confirm() {
        if (status != Status.CHECK) {
            throw new IllegalStateException("주문의 상태가 입금 확인이 아닙니다.");
        }
        this.status = Status.COOKING;
    }

    public void complete(LocalDateTime completedAt) {
        if (status != Status.COOKING) {
            throw new IllegalStateException("주문의 상태가 요리중이 아닙니다.");
        }
        this.status = Status.DONE;
        this.completedAt = completedAt;
    }

    public void cancel() {
        if (status == Status.DONE) {
            throw new IllegalStateException("완료된 주문은 취소할 수 없습니다.");
        }
        if (status == Status.CANCELED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }
        this.status = Status.CANCELED;
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
