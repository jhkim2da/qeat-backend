package com.example.demo.dto.order;

public record OrderItemRequest(
    Long menuId,
    int quantity
){
}
