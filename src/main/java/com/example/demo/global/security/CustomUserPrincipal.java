package com.example.demo.global.security;

import com.example.demo.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal {
    private Long id;
    private String studentNumber;
    private String name;
    private Role role;
}