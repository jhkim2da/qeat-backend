package com.example.demo.controller;

import com.example.demo.Service.BoothService;
import com.example.demo.dto.booth.BoothCreateRequest;
import com.example.demo.dto.booth.BoothDetailResponse;
import com.example.demo.dto.booth.BoothMyResponse;
import com.example.demo.global.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/booths")
public class BoothController {

    private final BoothService boothService;

    public BoothController(BoothService boothService) {
        this.boothService = boothService;
    }

    @PostMapping
    public ResponseEntity<Long> createBooth(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody BoothCreateRequest request
    ) {
        Long ownerId = principal.getId();
        Long boothId = boothService.createBooth(ownerId, request);

        return ResponseEntity
                .created(URI.create("/api/booths/my/" + boothId))
                .body(boothId);
    }

    @GetMapping("/my")
    public ResponseEntity<List<BoothMyResponse>> getMyBooth(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long ownerId = principal.getId();
        List<BoothMyResponse> booths = boothService.getMyBooth(ownerId);
        return ResponseEntity.ok(booths);
    }

    @GetMapping("/my/{boothId}")
    public ResponseEntity<BoothDetailResponse> getBooth(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long boothId
    ) {
        Long ownerId = principal.getId();
        BoothDetailResponse response = boothService.getMyBoothDetail(ownerId, boothId);
        return ResponseEntity.ok(response);
    }
}