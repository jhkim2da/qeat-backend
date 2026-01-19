package com.example.demo.controller;

import com.example.demo.Service.OrderService;
import com.example.demo.dto.order.OrderCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
