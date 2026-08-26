package com.qeat.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull(message = "메뉴를 선택해야 합니다.")
        Long menuId,
        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        int quantity
) {
}
