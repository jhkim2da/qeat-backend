package com.example.demo.dto.menu;


import com.example.demo.domain.Category;

public record MenuCreateRequest(
        String name,
        int price,
        String description,
        String imageUrl,
        Category category
) {
}
