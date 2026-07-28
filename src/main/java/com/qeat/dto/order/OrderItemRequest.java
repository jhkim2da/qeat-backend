package com.qeat.dto.order;

public record OrderItemRequest(
    Long menuId,
    int quantity
){
}
