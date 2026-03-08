package com.example.demo.Service;

import com.example.demo.Repository.BoothRepository;
import com.example.demo.Repository.MenuRepository;
import com.example.demo.Repository.OrderItemRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.domain.Menu;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderItem;
import com.example.demo.dto.order.OrderCreateRequest;
import com.example.demo.dto.order.OrderItemRequest;
import com.example.demo.dto.order.OrderItemResponse;
import com.example.demo.dto.order.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final BoothRepository boothRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository,
                        BoothRepository boothRepository,
                        MenuRepository menuRepository,
                        OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.boothRepository = boothRepository;
        this.menuRepository = menuRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public Long createOrder(Long boothId, OrderCreateRequest request) {
        boothRepository.findById(boothId)
                .orElseThrow(() -> new IllegalArgumentException("부스가 존재하지 않음"));

        Map<Long, Menu> menuMap = new HashMap<>();
        int totalPrice = 0;

        for (OrderItemRequest item : request.items()) {
            if (item.quantity() <= 0) {
                throw new IllegalArgumentException("수량은 1 이상이여야 합니다.");
            }

            Menu menu = menuRepository.findById(item.menuId())
                    .orElseThrow(() -> new IllegalArgumentException("메뉴 없음"));

            if (!boothId.equals(menu.getBooth().getId())) {
                throw new IllegalStateException("다른 부스의 상품입니다");
            }

            if (menu.isSoldOut()) {
                throw new IllegalStateException("품절 메뉴");
            }

            totalPrice += menu.getPrice() * item.quantity();
            menuMap.put(menu.getId(), menu);
        }

        Order order = Order.create(boothId, request.tableId(), totalPrice);
        orderRepository.save(order);

        for (OrderItemRequest item : request.items()) {
            Menu menu = menuMap.get(item.menuId());

            OrderItem orderItem = OrderItem.create(
                    order.getId(),
                    menu,
                    item.quantity(),
                    menu.getPrice()
            );

            orderItemRepository.save(orderItem);
        }

        return order.getId();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(Long boothId) {
        List<Order> orders = orderRepository.findByBoothId(boothId);

        return orders.stream()
                .map(order -> {
                    List<OrderItemResponse> itemResponses = orderItemRepository.findByOrderId(order.getId())
                            .stream()
                            .map(OrderItemResponse::from)
                            .toList();

                    return OrderResponse.from(order, itemResponses);
                })
                .toList();
    }
}