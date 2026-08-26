package com.qeat.service;

import com.qeat.domain.Bank;
import com.qeat.domain.Booth;
import com.qeat.domain.BoothStatus;
import com.qeat.domain.Role;
import com.qeat.dto.auth.AuthUser;
import com.qeat.repository.BoothRepository;
import com.qeat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoothServiceTest {

    @Mock
    private BoothRepository boothRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BoothService boothService;

    private Booth booth;

    @BeforeEach
    void setUp() {
        booth = Booth.create("축제부스", "설명", 1L, Bank.KB, "1234567890");
        booth.approve();
        ReflectionTestUtils.setField(booth, "id", 1L);
    }

    @Test
    void getOperableBooth_allowsOwner() {
        when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
        AuthUser owner = new AuthUser(1L, Role.OPERATOR);

        Booth result = boothService.getOperableBooth(1L, owner);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBoothStatus()).isEqualTo(BoothStatus.APPROVED);
    }

    @Test
    void getOperableBooth_allowsAdmin() {
        when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
        AuthUser admin = new AuthUser(99L, Role.ADMIN);

        Booth result = boothService.getOperableBooth(1L, admin);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getOperableBooth_deniesOtherOperator() {
        when(boothRepository.findById(1L)).thenReturn(Optional.of(booth));
        AuthUser otherOperator = new AuthUser(2L, Role.OPERATOR);

        assertThatThrownBy(() -> boothService.getOperableBooth(1L, otherOperator))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("해당 부스에 접근할 권한이 없습니다.");
    }

    @Test
    void getOperableBooth_rejectsUnapprovedBooth() {
        Booth pending = Booth.create("대기부스", "설명", 1L, Bank.KB, "1234567890");
        ReflectionTestUtils.setField(pending, "id", 2L);
        when(boothRepository.findById(2L)).thenReturn(Optional.of(pending));
        AuthUser owner = new AuthUser(1L, Role.OPERATOR);

        assertThatThrownBy(() -> boothService.getOperableBooth(2L, owner))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("승인된 부스만 운영할 수 있습니다.");
    }
}
