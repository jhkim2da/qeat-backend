package com.example.demo.dto.booth;

import com.example.demo.domain.Booth;
import com.example.demo.dto.menu.MenuResponse;

import java.time.LocalDate;
import java.util.List;

public record BoothDetailResponse(
        Long boothId,
        String name,
        String description,
        LocalDate createdAt,
        List<MenuResponse> menus
) {
    public static BoothDetailResponse from(Booth booth, List<MenuResponse> menus) {
        return new BoothDetailResponse(
                booth.getId(),
                booth.getName(),
                booth.getDescription(),
                booth.getCreatedAt(),
                menus
        );
    }
}
