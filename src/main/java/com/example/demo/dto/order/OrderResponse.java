package com.example.demo.dto.order;

import com.example.demo.domain.Order;
import com.example.demo.domain.Status;


import java.util.List;

public class OrderResponse {
    private Long orderId;
    private Long tableId;
    private Integer tableNumber;
    private Status status;
    private int totalPrice;
    private List<OrderItemResponse> items;

    public OrderResponse(Long orderId, Long tableId, Integer tableNumber, Status status, int totalPrice, List<OrderItemResponse> items) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.status = status;
        this.totalPrice = totalPrice;
        this.items = items;
    }


    public static OrderResponse from(Order order, Integer tableNumber, List<OrderItemResponse> items) {
        return new OrderResponse(
                order.getId(),
                order.getTableId(),
                tableNumber,
                order.getStatus(),
                order.getTotalPrice(),
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

    public List<OrderItemResponse> getItems() {
        return items;
    }
}
