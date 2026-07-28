package com.qeat.dto.order;

import java.time.LocalTime;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderSalesDto {
    private LocalTime orderTime; // 주문 시간
    private Integer amount;      // 주문 금액

}
