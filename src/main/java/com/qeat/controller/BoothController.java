package com.qeat.controller;

import com.qeat.domain.Booth;
import com.qeat.dto.booth.BoothCreateRequest;
import com.qeat.dto.booth.BoothDetailResponse;
import com.qeat.dto.booth.BoothMyResponse;
import com.qeat.dto.booth.BoothOpenStatusRequest;
import com.qeat.dto.booth.BoothOperatingTimeRequest;
import com.qeat.dto.booth.BoothOperatorDetailResponse;
import com.qeat.dto.booth.BoothOperatorResponse;
import com.qeat.dto.booth.BoothResponse;
import com.qeat.global.security.CustomUserPrincipal;
import com.qeat.service.BoothService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Long boothId = boothService.createBooth(principal.getId(), request);
        return ResponseEntity
                .created(URI.create("/api/booths/my/" + boothId))
                .body(boothId);
    }

    @GetMapping("/my")
    public ResponseEntity<List<BoothMyResponse>> getMyBooth(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(boothService.getMyBooth(principal.getId()));
    }

    @GetMapping("/my/approved")
    public ResponseEntity<List<BoothMyResponse>> getMyApprovedBooths(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(boothService.getMyApprovedBooths(principal.getId()));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BoothMyResponse>> getPendingBooths(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(boothService.getPendingBooths(principal.getId()));
    }

    @GetMapping("/operators")
    public ResponseEntity<List<BoothOperatorResponse>> getBoothOperators(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(boothService.getBoothOperators(principal.getId()));
    }

    @GetMapping("/operators/{operatorId}")
    public ResponseEntity<BoothOperatorDetailResponse> getBoothOperatorDetail(
            @PathVariable Long operatorId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(boothService.getBoothOperatorDetail(operatorId, principal.getId()));
    }

    @GetMapping("/my/{boothId}")
    public ResponseEntity<BoothDetailResponse> getBooth(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long boothId
    ) {
        return ResponseEntity.ok(boothService.getMyBoothDetail(principal.getId(), boothId));
    }

    @PutMapping("/{boothId}")
    public ResponseEntity<BoothResponse> updateBooth(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody BoothCreateRequest request
    ) {
        Booth booth = boothService.updateBooth(principal.getId(), boothId, request);
        return ResponseEntity.ok(BoothResponse.from(booth));
    }

    @DeleteMapping("/{boothId}")
    public ResponseEntity<Void> deleteBooth(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        boothService.deleteBooth(boothId, principal.toAuthUser());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{boothId}/suspend")
    public ResponseEntity<BoothResponse> suspendBooth(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Booth booth = boothService.suspendBooth(boothId, principal.getId());
        return ResponseEntity.ok(BoothResponse.from(booth));
    }

    @PatchMapping("/{boothId}/open-status")
    public ResponseEntity<Boolean> updateOpenStatus(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody BoothOpenStatusRequest request
    ) {
        Boolean open = boothService.updateOpenStatus(principal.getId(), boothId, request.open());
        return ResponseEntity.ok(open);
    }

    @PatchMapping("/{boothId}/operating-time")
    public ResponseEntity<BoothResponse> updateOperatingTime(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody BoothOperatingTimeRequest request
    ) {
        Booth booth = boothService.updateOperatingTime(
                boothId,
                principal.toAuthUser(),
                request.openTime(),
                request.closeTime()
        );
        return ResponseEntity.ok(BoothResponse.from(booth));
    }

    @DeleteMapping("/{boothId}/operating-time")
    public ResponseEntity<BoothResponse> clearOperatingTime(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Booth booth = boothService.clearOperatingTime(boothId, principal.toAuthUser());
        return ResponseEntity.ok(BoothResponse.from(booth));
    }

    @PatchMapping("/{boothId}/approve")
    public void approveBooth(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        boothService.approveBooth(boothId, principal.getId());
    }

    @PatchMapping("/{boothId}/reject")
    public void rejectBooth(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        boothService.rejectBooth(boothId, principal.getId());
    }
}
