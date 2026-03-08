package com.example.demo.dto.booth;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BoothCreateRequest(

        @NotBlank
        @Size(max = 50)
        String name,
        @Size(max = 255)
        String description
) {
}
