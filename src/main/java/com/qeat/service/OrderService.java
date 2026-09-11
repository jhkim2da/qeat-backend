package com.qeat.service;

import com.qeat.domain.Booth;
import com.qeat.domain.BoothTable;
import com.qeat.domain.Menu;
import com.qeat.domain.Order;
import com.qeat.domain.OrderItem;
import com.qeat.domain.Status;
import com.qeat.dto.auth.AuthUser;
import com.qeat.dto.order.DailySalesResponse;
import com.qeat.dto.order.OrderCreateRequest;
import com.qeat.dto.order.OrderItemRequest;
import com.qeat.dto.order.OrderItemResponse;
import com.qeat.dto.order.OrderResponse;
import com.qeat.dto.order.OrderSalesDto;
import com.qeat.dto.order.PublicOrderCreateRequest;
import com.qeat.dto.order.SalesSummaryResponse;
import com.qeat.repository.BoothRepository;
import com.qeat.repository.BoothTableRepository;
import com.qeat.repository.MenuRepository;
import com.qeat.repository.OrderItemRepository;
import com.qeat.repository.OrderRepository;
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
    private final BoothTableRepository boothTableRepository;
    private final BoothService boothService;

    public OrderService(
            OrderRepository orderRepository,
            BoothRepository boothRepository,
            MenuRepository menuRepository,
            OrderItemRepository orderItemRepository,
            BoothTableRepository boothTableRepository,
            BoothService boothService
    ) {
        this.orderRepository = orderRepository;
        this.boothRepository = boothRepository;
        this.menuRepository = menuRepository;
        this.orderItemRepository = orderItemRepository;
        this.boothTableRepository = boothTableRepository;
        this.boothService = boothService;
    }

    @Transactional
    public Long createOrder(Long boothId, OrderCreateRequest request, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);
        return createOrder(boothId, request);
    }

    @Transactional
    public Long createOrderByTableToken(String tableToken, PublicOrderCreateRequest request) {
        BoothTable table = boothTableRepository.findByTableTokenAndActiveTrue(tableToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 테이블 QR입니다."));
        return createOrder(table.getBooth().getId(), new OrderCreateRequest(table.getId(), request.items()));
    }

    @Transactional
    public Long createOrder(Long boothId, OrderCreateRequest request) {
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new IllegalArgumentException("부스를 찾을 수 없습니다."));
        BoothTable table = boothTableRepository.findById(request.tableId())
                .orElseThrow(() -> new IllegalArgumentException("테이블이 존재하지 않습니다."));

        if (!table.isActive()) {
            throw new IllegalStateException("비활성화된 테이블입니다.");
        }
        if (!table.getBooth().getId().equals(boothId)) {
            throw new IllegalStateException("해당 부스의 테이블이 아닙니다.");
        }
        if (!booth.canOrder()) {
            throw new IllegalStateException("현재 영업중이 아닙니다.");
        }

        Map<Long, Menu> menuMap = new HashMap<>();
        int totalPrice = 0;
        List<Long> lockedMenuIds = request.items().stream()
                .map(OrderItemRequest::menuId)
                .distinct()
                .sorted()
                .toList();
        for (Long menuId : lockedMenuIds) {
            Menu menu = menuRepository.findByIdForUpdate(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다."));
            menuMap.put(menuId, menu);
        }
        for (OrderItemRequest item : request.items()) {
            if (item.quantity() <= 0) {
                throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
            }

            Menu menu = menuMap.get(item.menuId());
            if (menu == null) {
                throw new IllegalArgumentException("메뉴가 존재하지 않습니다.");
            }
            if (!boothId.equals(menu.getBooth().getId())) {
                throw new IllegalStateException("해당 부스의 메뉴가 아닙니다.");
            }
            if (menu.isSoldOut()) {
                throw new IllegalStateException("품절된 메뉴입니다.");
            }

            totalPrice += menu.getPrice() * item.quantity();
        }

        Order order = Order.create(boothId, request.tableId(), totalPrice);
        orderRepository.save(order);

        for (OrderItemRequest item : request.items()) {
            Menu menu = menuMap.get(item.menuId());
            orderItemRepository.save(OrderItem.create(
                    order.getId(),
                    menu,
                    item.quantity(),
                    menu.getPrice()
            ));
        }

        return order.getId();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(Long boothId, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);
        List<Order> orders = orderRepository.findByBoothIdOrderByCreatedAtDescIdDesc(boothId);

        return orders.stream()
                .map(order -> {
                    Integer tableNumber = boothTableRepository.findById(order.getTableId())
                            .map(BoothTable::getTableNumber)
                            .orElse(null);
                    List<OrderItemResponse> itemResponses = orderItemRepository.findByOrderId(order.getId())
                            .stream()
                            .map(OrderItemResponse::from)
                            .toList();
                    return OrderResponse.from(order, tableNumber, itemResponses);
                })
                .toList();
    }

    @Transactional
    public void confirmOrder(Long boothId, Long orderId, AuthUser authUser) {
        getOperableOrder(boothId, orderId, authUser).confirm();
    }

    @Transactional
    public void completeOrder(Long boothId, Long orderId, AuthUser authUser) {
        getOperableOrder(boothId, orderId, authUser).complete(LocalDateTime.now());
    }

    @Transactional
    public void cancelOrder(Long boothId, Long orderId, AuthUser authUser) {
        getOperableOrder(boothId, orderId, authUser).cancel();
    }

    @Transactional(readOnly = true)
    public SalesSummaryResponse getSalesSummary(
            Long boothId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            AuthUser authUser
    ) {
        boothService.getOperableBooth(boothId, authUser);
        List<Order> orders = orderRepository.findByBoothIdAndStatusAndCompletedAtBetween(
                boothId,
                Status.DONE,
                startDate,
                endDate
        );

        int totalSales = orders.stream().mapToInt(Order::getTotalPrice).sum();
        Map<LocalDate, List<Order>> groupedByDate = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getCompletedAt().toLocalDate()));

        List<DailySalesResponse> dailySales = groupedByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<Order> dailyOrders = entry.getValue();
                    List<OrderSalesDto> orderSales = dailyOrders.stream()
                            .sorted(Comparator.comparing(Order::getCompletedAt))
                            .map(order -> new OrderSalesDto(
                                    order.getCompletedAt().toLocalTime(),
                                    order.getTotalPrice()
                            ))
                            .toList();
                    return new DailySalesResponse(entry.getKey(), dailyOrders.size(), orderSales);
                })
                .toList();

        return new SalesSummaryResponse(totalSales, orders.size(), dailySales);
    }

    private Order getOperableOrder(Long boothId, Long orderId, AuthUser authUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("상태를 바꿀 주문이 없습니다."));
        Booth booth = boothService.getOperableBooth(boothId, authUser);
        if (!order.getBoothId().equals(booth.getId())) {
            throw new AccessDeniedException("해당 부스의 주문이 아닙니다.");
        }
        return order;
    }
}
