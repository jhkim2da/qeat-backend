package com.qeat.dto.booth;

import java.time.LocalTime;

public record BoothOperatingTimeRequest(
        LocalTime openTime,
        LocalTime closeTime
) {
}