package com.qeat.dto.order;

import java.util.List;

public record SalesSummaryResponse(
        int totalSales,
        int totalOrderCount,
        List<DailySalesResponse> dailySales
) {
}
