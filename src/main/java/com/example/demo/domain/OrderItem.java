package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    private int quantity;

    @Column(name = "price_at_order")
    private int priceAtOrder;

    @Column(name = "menu_id")
    private Long menuId;

    protected OrderItem() {}
}
