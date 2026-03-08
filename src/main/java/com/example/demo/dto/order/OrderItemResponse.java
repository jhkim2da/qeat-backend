package com.example.demo.dto.order;

import com.example.demo.domain.OrderItem;

public class OrderItemResponse {
    private String menuName;
    private Integer quantity;
    private Integer price;

    public OrderItemResponse(String menuName, Integer quantity, Integer price) {
        this.menuName = menuName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getMenuName() {
        return menuName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getPrice() {
        return price;
    }
    public static OrderItemResponse from(OrderItem orderItem) {

        return new OrderItemResponse(
                orderItem.getMenu().getName(),
                orderItem.getQuantity(),
                orderItem.getPriceAtOrder()
        );
    }


}
