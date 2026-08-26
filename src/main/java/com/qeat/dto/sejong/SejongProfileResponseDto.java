package com.qeat.dto.sejong;

public record SejongProfileResponseDto(
        String major,
        String studentId,
        String name,
        Integer gradeLevel
) {
    public static SejongProfileResponseDto of(String major, String studentId, String name, String gradeLevel) {
        return new SejongProfileResponseDto(major, studentId, name, extractNumber(gradeLevel));
    }

    private static Integer extractNumber(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String numberOnly = value.replaceAll("[^0-9]", "");
        if (numberOnly.isBlank()) {
            return null;
        }

        return Integer.parseInt(numberOnly);
    }
}
