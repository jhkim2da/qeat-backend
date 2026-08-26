package com.qeat.controller;

import com.qeat.dto.order.OrderCreateRequest;
import com.qeat.dto.order.OrderResponse;
import com.qeat.dto.order.SalesSummaryResponse;
import com.qeat.global.security.CustomUserPrincipal;
import com.qeat.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Long> createOrder(
            @PathVariable Long boothId,
            @Valid @RequestBody OrderCreateRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long orderId = orderService.createOrder(boothId, request, principal.toAuthUser());
        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/api/booths/{boothId}/orders")
    public List<OrderResponse> getOrders(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return orderService.getOrders(boothId, principal.toAuthUser());
    }

    @PatchMapping("/api/orders/{orderId}/booths/{boothId}/confirm")
    public ResponseEntity<Void> confirmOrder(
            @PathVariable Long boothId,
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        orderService.confirmOrder(boothId, orderId, principal.toAuthUser());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/orders/{orderId}/booths/{boothId}/complete")
    public ResponseEntity<Void> completeOrder(
            @PathVariable Long boothId,
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        orderService.completeOrder(boothId, orderId, principal.toAuthUser());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/orders/{orderId}/booths/{boothId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long boothId,
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        orderService.cancelOrder(boothId, orderId, principal.toAuthUser());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/booths/{boothId}/sales-summary")
    public ResponseEntity<SalesSummaryResponse> getSalesSummary(
            @PathVariable Long boothId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        SalesSummaryResponse response = orderService.getSalesSummary(
                boothId,
                startDateTime,
                endDateTime,
                principal.toAuthUser()
        );
        return ResponseEntity.ok(response);
    }
}
