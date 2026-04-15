package com.example.demo.dto.auth;

import com.example.demo.domain.Role;

public record AuthUser(
        Long userId,
        Role role
) {
}