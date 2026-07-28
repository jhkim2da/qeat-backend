package com.qeat.dto.table;

import com.qeat.domain.BoothTable;
import com.qeat.dto.menu.MenuResponse;

import java.time.LocalTime;
import java.util.List;

public record PublicTableMenuResponse(
        Long boothId,
        String boothName,
        boolean open,
        LocalTime openTime,
        LocalTime closeTime,
        boolean canOrder,
        Long tableId,
        int tableNumber,
        String tableToken,
        List<MenuResponse> menus
) {
    public static PublicTableMenuResponse from(BoothTable table, List<MenuResponse> menus) {
        return new PublicTableMenuResponse(
                table.getBooth().getId(),
                table.getBooth().getName(),
                table.getBooth().isOpen(),
                table.getBooth().getOpenTime(),
                table.getBooth().getCloseTime(),
                table.getBooth().canOrder(table.getBooth()),
                table.getId(),
                table.getTableNumber(),
                table.getTableToken(),
                menus
        );
    }
}
