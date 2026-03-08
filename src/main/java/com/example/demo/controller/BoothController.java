package com.example.demo.controller;

import com.example.demo.Repository.MenuRepository;
import com.example.demo.Service.BoothService;
import com.example.demo.domain.Booth;
import com.example.demo.dto.booth.BoothCreateRequest;
import com.example.demo.dto.booth.BoothDetailResponse;
import com.example.demo.dto.booth.BoothMyResponse;
import com.example.demo.dto.menu.MenuResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/booths")
public class BoothController {
    private final BoothService boothService;
    private final MenuRepository menuRepository;

    public BoothController(BoothService boothService, MenuRepository menuRepository) {
        this.boothService = boothService;
        this.menuRepository = menuRepository;
    }

    @PostMapping
    public ResponseEntity<Long> createBooth( @Valid @RequestBody BoothCreateRequest request) {
        Long ownerId = 1L;
        Long boothId = boothService.createBooth(ownerId, request);
        return ResponseEntity
                .created(URI.create("/api/booths/" + boothId))
                .body(boothId);
    }
    @GetMapping("/my")
    public ResponseEntity<List<BoothMyResponse>> getMyBooth() {
        Long ownerId = 1L;
        List<BoothMyResponse> booths = boothService.getMyBooth(ownerId);
        return ResponseEntity.ok(booths);
    }
    @GetMapping("/my/{boothId}")
    public ResponseEntity<BoothDetailResponse> getBooth(@PathVariable Long boothId) {
        Long ownerId = 1L;
        BoothDetailResponse response = boothService.getMyBoothDetail(ownerId, boothId);
        return  ResponseEntity.ok(response);
    }

}
