package com.qeat.dto.booth;

import com.qeat.domain.Bank;
import com.qeat.domain.Booth;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class BoothResponse {

    private Long id;
    private Long boothId;
    private String name;
    private Bank bank;
    private String accountNumber;
    private String description;
    private boolean open;
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean canOrder;

    public static BoothResponse from(Booth booth) {
        return new BoothResponse(
                booth.getId(),
                booth.getId(),
                booth.getName(),
                booth.getBank(),
                booth.getAccountNumber(),
                booth.getDescription(),
                booth.isOpen(),
                booth.getOpenTime(),
                booth.getCloseTime(),
                booth.canOrder(booth)
        );
    }
}
