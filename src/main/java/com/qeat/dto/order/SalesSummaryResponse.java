package com.qeat.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SalesSummaryResponse {
    private int totalSales;
    private int totalOrderCount;
    private List<DailySalesResponse> dailySales;
}
