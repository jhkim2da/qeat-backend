package com.example.demo.controller;

import com.example.demo.Service.OrderService;
import com.example.demo.dto.order.OrderCreateRequest;
import com.example.demo.dto.order.OrderResponse;
import com.example.demo.dto.order.SalesSummaryResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

    @PatchMapping("/api/orders/{orderId}/confirm")
    public ResponseEntity<Void> confirmOrder(@PathVariable Long orderId) {
        orderService.confirmOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/orders/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(@PathVariable Long orderId) {
        orderService.completeOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
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
