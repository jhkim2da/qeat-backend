package com.example.demo.Service;

import com.example.demo.Repository.BoothRepository;
import com.example.demo.Repository.MenuRepository;
import com.example.demo.Repository.OrderItemRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.domain.*;
import com.example.demo.dto.auth.AuthUser;
import com.example.demo.dto.order.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final BoothRepository boothRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;
    private final BoothService boothService;

    public OrderService(OrderRepository orderRepository,
                        BoothRepository boothRepository,
                        MenuRepository menuRepository,
                        OrderItemRepository orderItemRepository, BoothService boothService) {
        this.orderRepository = orderRepository;
        this.boothRepository = boothRepository;
        this.menuRepository = menuRepository;
        this.orderItemRepository = orderItemRepository;
        this.boothService = boothService;
    }

    @Transactional
    public Long createOrder(Long boothId, OrderCreateRequest request) {
        Booth booth =  boothRepository.findById(boothId)
                .orElseThrow(() -> new IllegalArgumentException("부스가 존재하지 않음"));

        if (!booth.canOrder(booth)) {
            throw new IllegalStateException("현재 영업중이 아닙니다.");
        }

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

    @Transactional
    public void confirmOrder(Long boothId, Long orderId, AuthUser authUser) {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new IllegalArgumentException("상태를 바꿀 주문이 없습니다."));
        Booth booth = boothService.getOperableBooth(boothId, authUser);
        if (!order.getBoothId().equals(booth.getId())) {
            throw new AccessDeniedException("해당 부스의 주문이 아닙니다.");
        }
        if (order.getStatus() !=  Status.CHECK) {
            throw new IllegalStateException("주문의 상태가 입금 확인이 아닙니다.");
        }
        order.setStatus(Status.COOKING);
        orderRepository.save(order);
    }

    @Transactional
    public void completeOrder(Long boothId, Long orderId, AuthUser authUser) {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new IllegalArgumentException("상태를 바꿀 주문이 없습니다."));
        Booth booth = boothService.getOperableBooth(boothId, authUser);
        if (!order.getBoothId().equals(booth.getId())) {
            throw new AccessDeniedException("해당 부스의 주문이 아닙니다.");
        }
        if (order.getStatus() !=  Status.COOKING) {
            throw new IllegalStateException("주문의 상태가 요리중이 아닙니다.");
        }
        order.setStatus(Status.DONE);
        order.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long boothId, Long orderId, AuthUser authUser) {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new IllegalArgumentException("상태를 바꿀 주문이 없습니다."));
        Booth booth = boothService.getOperableBooth(boothId, authUser);
        if (!order.getBoothId().equals(booth.getId())) {
            throw new AccessDeniedException("해당 부스의 주문이 아닙니다.");
        }
        order.setStatus(Status.CANCELED);
        orderRepository.save(order);
    }

    public SalesSummaryResponse getSalesSummary(Long boothId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByBoothIdAndStatusAndCompletedAtBetween(
                boothId, Status.DONE, startDate, endDate
        );

        int totalSales = orders.stream()
                .mapToInt(Order::getTotalPrice)
                .sum();

        int totalOrderCount = orders.size();

        Map<LocalDate, List<Order>> groupedByDate = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getCompletedAt().toLocalDate()));

        List<DailySalesResponse> dailySales = groupedByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Order> dailyOrders = entry.getValue();

                    List<OrderSalesDto> orderSales = dailyOrders.stream()
                            .sorted(Comparator.comparing(Order::getCompletedAt))
                            .map(order -> new OrderSalesDto(
                                    order.getCompletedAt().toLocalTime(),
                                    order.getTotalPrice()
                            ))
                            .toList();

                    return new DailySalesResponse(
                            date,
                            dailyOrders.size(),
                            orderSales
                    );
                })
                .toList();

        return new SalesSummaryResponse(
                totalSales,
                totalOrderCount,
                dailySales
        );
    }



}