package com.example.demo.controller;

import com.example.demo.service.OrderService;
import com.example.demo.dto.auth.AuthUser;
import com.example.demo.dto.order.OrderCreateRequest;
import com.example.demo.dto.order.OrderResponse;
import com.example.demo.dto.order.SalesSummaryResponse;
import com.example.demo.global.security.CustomUserPrincipal;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping("/api/booths/{boothId}/orders")
    public ResponseEntity<Long> createOrder(@PathVariable Long boothId, @RequestBody OrderCreateRequest request) {
        Long orderId = orderService.createOrder(boothId, request);
        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/api/booths/{boothId}/orders")
    public  List<OrderResponse> getOrders(@PathVariable Long boothId) {
        return orderService.getOrders(boothId);
    }

    @PatchMapping("/api/orders/{orderId}/booths/{boothId}/confirm")
    public ResponseEntity<Void> confirmOrder(
            @PathVariable Long boothId,
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails
            ) {
        AuthUser authUser = new AuthUser(userDetails.getId(), userDetails.getRole());
        orderService.confirmOrder(boothId, orderId, authUser);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/orders/{orderId}/booths/{boothId}/complete")
    public ResponseEntity<Void> completeOrder(
            @PathVariable Long boothId,
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails) {
        AuthUser authUser = new AuthUser(userDetails.getId(), userDetails.getRole());
        orderService.completeOrder(boothId, orderId, authUser);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/orders/{orderId}/booths/{boothId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long boothId,
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails) {
        AuthUser authUser = new AuthUser(userDetails.getId(), userDetails.getRole());
        orderService.cancelOrder(boothId, orderId, authUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/booths/{boothId}/sales-summary")
    public ResponseEntity<SalesSummaryResponse> getSalesSummary(
            @PathVariable Long boothId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        SalesSummaryResponse response = orderService.getSalesSummary(boothId, startDateTime, endDateTime);
        return ResponseEntity.ok(response);
    }
}
