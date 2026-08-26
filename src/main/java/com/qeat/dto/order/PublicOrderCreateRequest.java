package com.qeat.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PublicOrderCreateRequest(
        @NotEmpty(message = "주문 항목이 비어 있습니다.")
        List<@Valid OrderItemRequest> items
) {
}
