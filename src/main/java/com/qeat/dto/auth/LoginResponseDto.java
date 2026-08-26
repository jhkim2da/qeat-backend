package com.qeat.dto.auth;

import com.qeat.domain.Role;
import com.qeat.domain.User;

public record LoginResponseDto(
        Long id,
        String studentNumber,
        String name,
        String major,
        Integer grade,
        Role role
) {
    public static LoginResponseDto from(User user) {
        return new LoginResponseDto(
                user.getId(),
                user.getStudentNumber(),
                user.getName(),
                user.getMajor(),
                user.getGrade(),
                user.getRole()
        );
    }
}
