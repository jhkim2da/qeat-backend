package com.qeat.dto.order;

import com.qeat.domain.OrderItem;

public record OrderItemResponse(
        String menuName,
        Integer quantity,
        Integer price
) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getMenu().getName(),
                orderItem.getQuantity(),
                orderItem.getPriceAtOrder()
        );
    }
}
