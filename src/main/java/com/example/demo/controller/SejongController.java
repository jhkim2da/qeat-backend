package com.example.demo.controller;

import com.example.demo.Service.SejongUserService;
import com.example.demo.dto.auth.LoginResponseDto;
import com.example.demo.dto.sejong.SejongLoginRequestDto;
import com.example.demo.exception.LoginFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SejongController {

    private final SejongUserService sejongUserService;

    public SejongController(SejongUserService sejongUserService) {
        this.sejongUserService = sejongUserService;
    }

    @PostMapping("/api/sejong/login")
    public ResponseEntity<?> login(@RequestBody SejongLoginRequestDto loginRequestDto) {
        try {
            LoginResponseDto response = sejongUserService.login(loginRequestDto);
            return ResponseEntity.ok(response);
        } catch (LoginFailedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
