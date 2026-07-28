package com.qeat.dto.booth;

import java.util.List;

public record BoothOperatorDetailResponse(
        Long userId,
        String name,
        Integer grade,
        String studentNumber,
        String major,
        List<BoothMyResponse> booths
) {
}
