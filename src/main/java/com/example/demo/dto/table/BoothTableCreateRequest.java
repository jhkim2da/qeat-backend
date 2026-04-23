package com.example.demo.dto.table;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BoothTableCreateRequest(

        @NotNull(message = "테이블 번호는 필수입니다.")
        @Min(value = 1, message = "테이블 번호는 1 이상이어야 합니다.")
        Integer tableNumber
) {
}