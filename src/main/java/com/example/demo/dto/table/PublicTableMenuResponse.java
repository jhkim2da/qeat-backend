package com.example.demo.dto.table;

import com.example.demo.domain.BoothTable;
import com.example.demo.dto.menu.MenuResponse;

import java.util.List;

public record PublicTableMenuResponse(
        Long boothId,
        String boothName,
        Long tableId,
        int tableNumber,
        String tableToken,
        List<MenuResponse> menus
) {
    public static PublicTableMenuResponse from(BoothTable table, List<MenuResponse> menus) {
        return new PublicTableMenuResponse(
                table.getBooth().getId(),
                table.getBooth().getName(),
                table.getId(),
                table.getTableNumber(),
                table.getTableToken(),
                menus
        );
    }
}
