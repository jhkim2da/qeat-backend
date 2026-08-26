package com.qeat.global.security;

import com.qeat.domain.Role;
import com.qeat.dto.auth.AuthUser;

public class CustomUserPrincipal {

    private final Long id;
    private final String studentNumber;
    private final String name;
    private final Role role;

    public CustomUserPrincipal(Long id, String studentNumber, String name, Role role) {
        this.id = id;
        this.studentNumber = studentNumber;
        this.name = name;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public AuthUser toAuthUser() {
        return new AuthUser(id, role);
    }
}
