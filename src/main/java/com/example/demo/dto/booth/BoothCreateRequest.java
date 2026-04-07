package com.example.demo.dto.booth;


import com.example.demo.domain.Bank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BoothCreateRequest(

        @NotBlank
        @Size(max = 50)
        String name,

        @NotNull
        Bank bank,

        @NotBlank
        @Size(max = 50)
        String accountNumber,

        @Size(max = 255)
        String description
) {
}
