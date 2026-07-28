package com.qeat.dto.auth;

import com.qeat.domain.Role;

public record AuthUser(
        Long userId,
        Role role
) {
}