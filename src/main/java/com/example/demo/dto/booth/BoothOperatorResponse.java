package com.example.demo.dto.booth;

public record BoothOperatorResponse(
        Long userId,
        String name,
        String studentNumber,
        String major,
        long boothCount
) {
}
