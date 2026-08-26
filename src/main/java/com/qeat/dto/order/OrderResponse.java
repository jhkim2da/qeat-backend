package com.qeat.dto.order;

import com.qeat.domain.Order;
import com.qeat.domain.Status;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long tableId,
        Integer tableNumber,
        Status status,
        int totalPrice,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        List<OrderItemResponse> items
) {
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
}
