package com.qeat.dto.order;

import com.qeat.domain.Order;
import com.qeat.domain.Status;


import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long orderId;
    private Long tableId;
    private Integer tableNumber;
    private Status status;
    private int totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private List<OrderItemResponse> items;

    public OrderResponse(Long orderId,
                         Long tableId,
                         Integer tableNumber,
                         Status status,
                         int totalPrice,
                         LocalDateTime createdAt,
                         LocalDateTime completedAt,
                         List<OrderItemResponse> items) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.items = items;
    }


    public static OrderResponse from(Order order, Integer tableNumber, List<OrderItemResponse> items) {
        return new OrderResponse(
                order.getId(),
                order.getTableId(),
                tableNumber,
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getCompletedAt(),
                items
        );
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getTableId() {
        return tableId;
    }

    public Integer getTableNumber() {
        return tableNumber;
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

    public List<OrderItemResponse> getItems() {
        return items;
    }
}
