package com.qeat.controller;

import com.qeat.service.SejongUserService;
import com.qeat.service.UserService;
import com.qeat.domain.User;
import com.qeat.dto.auth.LoginResponseDto;
import com.qeat.dto.sejong.SejongLoginRequestDto;
import com.qeat.exception.ErrorResponse;
import com.qeat.global.security.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final SejongUserService sejongUserService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody SejongLoginRequestDto loginRequestDto,
            HttpServletRequest request
    ) {
        LoginResponseDto response = sejongUserService.login(loginRequestDto, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("로그인되지 않았습니다."));
        }

        return ResponseEntity.ok(Map.of(
                "id", principal.getId(),
                "studentNumber", principal.getStudentNumber(),
                "name", principal.getName(),
                "role", principal.getRole().name()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(Map.of(
                "message", "로그아웃 성공"
        ));
    }

    @PostMapping("/bootstrap-admin")
    public ResponseEntity<?> bootstrapAdmin(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("로그인되지 않았습니다."));
        }

        User user = userService.bootstrapAdmin(principal.getId());
        return ResponseEntity.ok(LoginResponseDto.from(user));
    }
}
