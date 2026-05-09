package com.example.demo.dto.booth;

import com.example.demo.domain.Booth;
import com.example.demo.domain.Bank;
import com.example.demo.domain.BoothStatus;

import java.time.LocalDate;

public record BoothDetailResponse(
        Long boothId,
        String name,
        String description,
        Bank bank,
        String accountNumber,
        BoothStatus boothStatus,
        LocalDate createdAt
) {
    public static BoothDetailResponse from(Booth booth) {
        return new BoothDetailResponse(
                booth.getId(),
                booth.getName(),
                booth.getDescription(),
                booth.getBank(),
                booth.getAccountNumber(),
                booth.getBoothStatus(),
                booth.getCreatedAt()
        );
    }
}
