package com.qeat.dto.order;

import java.time.LocalDate;
import java.util.List;

public record DailySalesResponse(
        LocalDate date,
        int totalOrderCount,
        List<OrderSalesDto> orders
) {
}
