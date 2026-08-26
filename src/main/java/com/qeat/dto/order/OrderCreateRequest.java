package com.qeat.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequest(
        @NotNull(message = "테이블을 선택해야 합니다.")
        Long tableId,
        @NotEmpty(message = "주문 항목이 비어 있습니다.")
        List<@Valid OrderItemRequest> items
) {
}
