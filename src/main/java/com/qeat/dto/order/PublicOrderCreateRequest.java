package com.qeat.dto.order;

import java.util.List;

public record PublicOrderCreateRequest(
        List<OrderItemRequest> items
) {
}
