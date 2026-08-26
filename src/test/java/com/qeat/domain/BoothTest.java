package com.qeat.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BoothTest {

    @Test
    void canOrder_isFalseWhenPending() {
        Booth booth = Booth.create("축제부스", "설명", 1L, Bank.KB, "1234567890");

        assertThat(booth.canOrder()).isFalse();
    }

    @Test
    void canOrder_isTrueWhenApprovedAndOpen() {
        Booth booth = Booth.create("축제부스", "설명", 1L, Bank.KB, "1234567890");
        booth.approve();

        assertThat(booth.canOrder()).isTrue();
    }

    @Test
    void canOrder_isFalseWhenClosed() {
        Booth booth = Booth.create("축제부스", "설명", 1L, Bank.KB, "1234567890");
        booth.approve();
        booth.changeOpenStatus(false);

        assertThat(booth.canOrder()).isFalse();
    }

    @Test
    void canOrder_isFalseWhenSuspended() {
        Booth booth = Booth.create("축제부스", "설명", 1L, Bank.KB, "1234567890");
        booth.approve();
        booth.suspend();

        assertThat(booth.canOrder()).isFalse();
        assertThat(booth.getBoothStatus()).isEqualTo(BoothStatus.SUSPENDED);
        assertThat(booth.isOpen()).isFalse();
    }
}
