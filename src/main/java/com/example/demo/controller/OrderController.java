package com.example.demo.controller;

import com.example.demo.Service.OrderService;
import com.example.demo.domain.Order;
import com.example.demo.dto.order.OrderCreateRequest;
import com.example.demo.dto.order.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
