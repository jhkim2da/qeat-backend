package com.qeat.controller;

import com.qeat.service.BoothService;
import com.qeat.domain.Booth;
import com.qeat.dto.auth.AuthUser;
import com.qeat.dto.booth.*;
import com.qeat.global.security.CustomUserPrincipal;
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

    @GetMapping("/my/approved")
    public ResponseEntity<List<BoothMyResponse>> getMyApprovedBooths(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long ownerId = principal.getId();
        List<BoothMyResponse> booths = boothService.getMyApprovedBooths(ownerId);
        return ResponseEntity.ok(booths);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BoothMyResponse>> getPendingBooths(
            @AuthenticationPrincipal CustomUserPrincipal userDetails
    ) {
        List<BoothMyResponse> booths = boothService.getPendingBooths(userDetails.getId());
        return ResponseEntity.ok(booths);
    }

    @GetMapping("/operators")
    public ResponseEntity<List<BoothOperatorResponse>> getBoothOperators(
            @AuthenticationPrincipal CustomUserPrincipal userDetails
    ) {
        List<BoothOperatorResponse> operators = boothService.getBoothOperators(userDetails.getId());
        return ResponseEntity.ok(operators);
    }

    @GetMapping("/operators/{operatorId}")
    public ResponseEntity<BoothOperatorDetailResponse> getBoothOperatorDetail(
            @PathVariable Long operatorId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails
    ) {
        BoothOperatorDetailResponse response = boothService.getBoothOperatorDetail(operatorId, userDetails.getId());
        return ResponseEntity.ok(response);
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
    @PutMapping("/{boothId}")
    public ResponseEntity<BoothResponse> updateBooth(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails,
            @Valid @RequestBody BoothCreateRequest request
    ) {
        Booth booth = boothService.updateBooth(userDetails.getId() , boothId, request);
        return ResponseEntity.ok(BoothResponse.from(booth));
    }

    @DeleteMapping("/{boothId}")
    public ResponseEntity<Void> deleteBooth(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails
    ) {
        AuthUser authUser = new AuthUser(userDetails.getId(), userDetails.getRole());
        boothService.deleteBooth(boothId, authUser);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{boothId}/suspend")
    public ResponseEntity<BoothResponse> suspendBooth(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails
    ) {
        Booth booth = boothService.suspendBooth(boothId, userDetails.getId());
        return ResponseEntity.ok(BoothResponse.from(booth));
    }

    @PatchMapping("/{boothId}/open-status")
    public ResponseEntity<Boolean> updateOpenStatus(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails,
            @RequestBody BoothOpenStatusRequest request
    ) {
        Boolean open = boothService.updateOpenStatus(
                userDetails.getId(),
                boothId,
                request.open()
        );
        return ResponseEntity.ok(open);
    }

    @PatchMapping("/{boothId}/operating-time")
    public ResponseEntity<BoothResponse> updateOperatingTime(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails,
            @RequestBody BoothOperatingTimeRequest request
    ) {
        AuthUser authUser = new AuthUser(userDetails.getId(), userDetails.getRole());
        Booth booth = boothService.updateOperatingTime(
                boothId,
                authUser,
                request.openTime(),
                request.closeTime()
        );

        return ResponseEntity.ok(BoothResponse.from(booth));
    }

    @DeleteMapping("/{boothId}/operating-time")
    public ResponseEntity<BoothResponse> clearOperatingTime(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails
    ) {
        AuthUser authUser = new AuthUser(userDetails.getId(), userDetails.getRole());
        Booth booth = boothService.clearOperatingTime(boothId, authUser);
        return ResponseEntity.ok(BoothResponse.from(booth));
    }

    @PatchMapping("/{boothId}/approve")
    public void approveBooth(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails
    ){
        boothService.approveBooth(boothId, userDetails.getId());
    }

    @PatchMapping("/{boothId}/reject")
    public void rejectBooth(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails
    ){
        boothService.rejectBooth(boothId, userDetails.getId());
    }
}
