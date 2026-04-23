package com.example.demo.dto.table;

import com.example.demo.domain.BoothTable;

public record BoothTableResponse(
        Long id,
        Long boothId,
        int tableNumber,
        String tableToken,
        String qrImageUrl,
        boolean active
) {
    public static BoothTableResponse from(BoothTable boothTable) {
        return new BoothTableResponse(
                boothTable.getId(),
                boothTable.getBooth().getId(),
                boothTable.getTableNumber(),
                boothTable.getTableToken(),
                boothTable.getQrImageUrl(),
                boothTable.isActive()
        );
    }
}