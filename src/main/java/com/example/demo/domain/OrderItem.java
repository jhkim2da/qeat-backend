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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    protected OrderItem() {}

    private OrderItem(Long orderId, Menu menu, int quantity, int priceAtOrder) {
        this.orderId = orderId;
        this.menu = menu;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }

    public static OrderItem create(Long orderId, Menu menu, int quantity, int priceAtOrder) {
        return new OrderItem(orderId, menu, quantity, priceAtOrder);
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPriceAtOrder() {
        return priceAtOrder;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPriceAtOrder(int priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}