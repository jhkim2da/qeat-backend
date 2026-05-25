package com.example.demo.dto.booth;

import com.example.demo.domain.Booth;
import com.example.demo.domain.Bank;
import com.example.demo.domain.BoothStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record BoothMyResponse(
        Long boothId,
        String name,
        String description,
        Bank bank,
        String accountNumber,
        BoothStatus boothStatus,
        boolean open,
        LocalTime openTime,
        LocalTime closeTime,
        boolean canOrder,
        LocalDate createdAt
) {
    public static BoothMyResponse from(Booth booth) {
        return new BoothMyResponse(
                booth.getId(),
                booth.getName(),
                booth.getDescription(),
                booth.getBank(),
                booth.getAccountNumber(),
                booth.getBoothStatus(),
                booth.isOpen(),
                booth.getOpenTime(),
                booth.getCloseTime(),
                booth.canOrder(booth),
                booth.getCreatedAt()
        );
    }
}
