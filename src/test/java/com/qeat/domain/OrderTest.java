package com.qeat.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void confirm_changesCheckToCooking() {
        Order order = Order.create(1L, 1L, 10000);

        order.confirm();

        assertThat(order.getStatus()).isEqualTo(Status.COOKING);
    }

    @Test
    void complete_changesCookingToDone() {
        Order order = Order.create(1L, 1L, 10000);
        order.confirm();
        LocalDateTime completedAt = LocalDateTime.of(2026, 8, 26, 12, 0);

        order.complete(completedAt);

        assertThat(order.getStatus()).isEqualTo(Status.DONE);
        assertThat(order.getCompletedAt()).isEqualTo(completedAt);
    }

    @Test
    void cancel_isAllowedFromCheck() {
        Order order = Order.create(1L, 1L, 10000);

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    void cancel_isAllowedFromCooking() {
        Order order = Order.create(1L, 1L, 10000);
        order.confirm();

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    void cancel_rejectsDoneOrder() {
        Order order = Order.create(1L, 1L, 10000);
        order.confirm();
        order.complete(LocalDateTime.now());

        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("완료된 주문은 취소할 수 없습니다.");
    }

    @Test
    void cancel_rejectsAlreadyCanceledOrder() {
        Order order = Order.create(1L, 1L, 10000);
        order.cancel();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 취소된 주문입니다.");
    }

    @Test
    void confirm_rejectsNonCheckOrder() {
        Order order = Order.create(1L, 1L, 10000);
        order.confirm();

        assertThatThrownBy(order::confirm)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("주문의 상태가 입금 확인이 아닙니다.");
    }

    @Test
    void complete_rejectsNonCookingOrder() {
        Order order = Order.create(1L, 1L, 10000);

        assertThatThrownBy(() -> order.complete(LocalDateTime.now()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("주문의 상태가 요리중이 아닙니다.");
    }
}
