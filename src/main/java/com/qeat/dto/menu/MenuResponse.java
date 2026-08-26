package com.qeat.dto.menu;

import com.qeat.domain.Category;
import com.qeat.domain.Menu;

public record MenuResponse(
        Long id,
        String name,
        String description,
        int price,
        Category category,
        String imageUrl,
        boolean soldOut
) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getDescription(),
                menu.getPrice(),
                menu.getCategory(),
                menu.getImageUrl(),
                menu.isSoldOut()
        );
    }
}
