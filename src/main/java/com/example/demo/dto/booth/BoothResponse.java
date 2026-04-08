package com.example.demo.dto.booth;

import com.example.demo.domain.Bank;
import com.example.demo.domain.Booth;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class BoothResponse {

    private Long id;
    private String name;
    private Bank bank;
    private String accountNumber;
    private String description;
    private boolean open;
    private LocalTime openTime;
    private LocalTime closeTime;

    public static BoothResponse from(Booth booth) {
        return new BoothResponse(
                booth.getId(),
                booth.getName(),
                booth.getBank(),
                booth.getAccountNumber(),
                booth.getDescription(),
                booth.isOpen(),
                booth.getOpenTime(),
                booth.getCloseTime()
        );
    }
}