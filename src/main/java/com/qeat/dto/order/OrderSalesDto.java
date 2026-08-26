package com.qeat.dto.order;

import java.time.LocalTime;

public record OrderSalesDto(
        LocalTime orderTime,
        Integer amount
) {
}
