package com.example.demo.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailySalesResponse {
    private LocalDate date;
    private int totalOrderCount;
    private List<OrderSalesDto> orders;
}
