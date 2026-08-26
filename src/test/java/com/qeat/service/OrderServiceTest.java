package com.qeat.service;

import com.qeat.domain.Bank;
import com.qeat.domain.Booth;
import com.qeat.domain.Order;
import com.qeat.domain.Role;
import com.qeat.domain.Status;
import com.qeat.dto.auth.AuthUser;
import com.qeat.repository.BoothRepository;
import com.qeat.repository.BoothTableRepository;
import com.qeat.repository.MenuRepository;
import com.qeat.repository.OrderItemRepository;
import com.qeat.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private BoothRepository boothRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private BoothTableRepository boothTableRepository;
    @Mock
    private BoothService boothService;

    @InjectMocks
    private OrderService orderService;

    private AuthUser owner;
    private Booth booth;

    @BeforeEach
    void setUp() {
        owner = new AuthUser(1L, Role.OPERATOR);
        booth = Booth.create("축제부스", "설명", 1L, Bank.KB, "1234567890");
        booth.approve();
        ReflectionTestUtils.setField(booth, "id", 1L);
    }

    @Test
    void cancelOrder_rejectsDoneOrder() {
        Order order = cookingOrder();
        order.complete(LocalDateTime.now());
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(boothService.getOperableBooth(1L, owner)).thenReturn(booth);

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 10L, owner))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("완료된 주문은 취소할 수 없습니다.");
        assertThat(order.getStatus()).isEqualTo(Status.DONE);
    }

    @Test
    void cancelOrder_allowsCheckOrder() {
        Order order = checkOrder();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(boothService.getOperableBooth(1L, owner)).thenReturn(booth);

        orderService.cancelOrder(1L, 10L, owner);

        assertThat(order.getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    void getSalesSummary_requiresOperableBooth() {
        AuthUser otherOperator = new AuthUser(2L, Role.OPERATOR);
        when(boothService.getOperableBooth(1L, otherOperator))
                .thenThrow(new AccessDeniedException("해당 부스에 접근할 권한이 없습니다."));

        assertThatThrownBy(() -> orderService.getSalesSummary(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                otherOperator
        )).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getSalesSummary_returnsEmptySummaryForOwner() {
        when(boothService.getOperableBooth(1L, owner)).thenReturn(booth);
        when(orderRepository.findByBoothIdAndStatusAndCompletedAtBetween(
                1L, Status.DONE, LocalDateTime.of(2026, 8, 1, 0, 0), LocalDateTime.of(2026, 8, 31, 23, 59, 59)
        )).thenReturn(List.of());

        var summary = orderService.getSalesSummary(
                1L,
                LocalDateTime.of(2026, 8, 1, 0, 0),
                LocalDateTime.of(2026, 8, 31, 23, 59, 59),
                owner
        );

        assertThat(summary.getTotalSales()).isEqualTo(0);
        assertThat(summary.getTotalOrderCount()).isEqualTo(0);
        verify(boothService).getOperableBooth(1L, owner);
    }

    @Test
    void confirmOrder_rejectsOrderFromAnotherBooth() {
        Order order = checkOrder();
        ReflectionTestUtils.setField(order, "boothId", 99L);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(boothService.getOperableBooth(1L, owner)).thenReturn(booth);

        assertThatThrownBy(() -> orderService.confirmOrder(1L, 10L, owner))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("해당 부스의 주문이 아닙니다.");
    }

    private Order checkOrder() {
        Order order = Order.create(1L, 3L, 12000);
        ReflectionTestUtils.setField(order, "id", 10L);
        return order;
    }

    private Order cookingOrder() {
        Order order = checkOrder();
        order.confirm();
        return order;
    }
}
