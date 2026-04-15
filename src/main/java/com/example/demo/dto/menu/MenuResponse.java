package com.example.demo.dto.menu;

import com.example.demo.domain.Category;
import com.example.demo.domain.Menu;


public record MenuResponse(
        Long id,
        String name,
        String description,
        int price,
        Category category,
        String imageUrl
) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getDescription(),
                menu.getPrice(),
                menu.getCategory(),
                menu.getImageUrl()
        );
    }
}
