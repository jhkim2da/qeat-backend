package com.qeat.dto.order;

import java.util.List;

public record OrderCreateRequest(
        Long tableId,
        List<OrderItemRequest> items
){
}
