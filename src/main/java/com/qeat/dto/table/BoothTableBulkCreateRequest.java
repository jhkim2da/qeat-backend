package com.qeat.dto.table;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BoothTableBulkCreateRequest(
        @NotNull(message = "추가할 테이블 개수는 필수입니다.")
        @Min(value = 1, message = "추가할 테이블 개수는 1 이상이어야 합니다.")
        Integer count
) {
}
