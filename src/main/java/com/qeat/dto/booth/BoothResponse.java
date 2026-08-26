package com.qeat.dto.booth;

import com.qeat.domain.Bank;
import com.qeat.domain.Booth;

import java.time.LocalTime;

public record BoothResponse(
        Long id,
        Long boothId,
        String name,
        Bank bank,
        String accountNumber,
        String description,
        boolean open,
        LocalTime openTime,
        LocalTime closeTime,
        boolean canOrder
) {
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
                booth.canOrder()
        );
    }
}
