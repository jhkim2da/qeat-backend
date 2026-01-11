package com.example.demo.dto;


import com.example.demo.domain.Category;

public record MenuCreateRequest(
        String name,
        int price,
        String description,
        String imageUrl,
        Category category
) {
}
