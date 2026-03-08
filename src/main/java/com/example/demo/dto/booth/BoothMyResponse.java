package com.example.demo.dto.booth;

import com.example.demo.domain.Booth;

import java.time.LocalDate;

public record BoothMyResponse(
        Long boothId,
        String name,
        String description,
        LocalDate createdAt
) {
    public static BoothMyResponse from(Booth booth) {
        return new BoothMyResponse(
                booth.getId(),
                booth.getName(),
                booth.getDescription(),
                booth.getCreatedAt()
        );
    }
}