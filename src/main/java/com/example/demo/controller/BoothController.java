package com.example.demo.controller;

import com.example.demo.Service.BoothService;
import com.example.demo.dto.booth.BoothCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BoothController {
    private final BoothService boothService;

    public BoothController(BoothService boothService) {
        this.boothService = boothService;
    }

    @PostMapping("/api/booths")
    public ResponseEntity<Long> createBooth(@RequestBody BoothCreateRequest request) {
        Long ownerId = 1L;
        Long boothId = boothService.createBooth(ownerId, request);
        return ResponseEntity.ok(boothId);
    }
}
