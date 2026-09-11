package com.qeat.service;

import com.qeat.domain.Bank;
import com.qeat.domain.Booth;
import com.qeat.domain.Category;
import com.qeat.domain.Menu;
import com.qeat.domain.Role;
import com.qeat.dto.auth.AuthUser;
import com.qeat.global.ImageUploader;
import com.qeat.global.redis.PublicMenuCache;
import com.qeat.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private ImageUploader imageUploader;
    @Mock
    private BoothService boothService;
    @Mock
    private PublicMenuCache publicMenuCache;

    @InjectMocks
    private MenuService menuService;

    private AuthUser owner;
    private Booth booth;
    private Menu menu;

    @BeforeEach
    void setUp() {
        owner = new AuthUser(1L, Role.OPERATOR);
        booth = Booth.create("축제부스", "설명", 1L, Bank.KB, "1234567890");
        booth.approve();
        ReflectionTestUtils.setField(booth, "id", 1L);
        menu = new Menu(booth, "떡볶이", "설명", 4000, "/uploads/a.png", Category.MAIN_FOOD);
        ReflectionTestUtils.setField(menu, "id", 10L);
    }

    @Test
    void getMenusByBooth_requiresOperableBooth() {
        when(boothService.getOperableBooth(1L, owner)).thenReturn(booth);
        when(menuRepository.findByBooth_Id(1L)).thenReturn(List.of(menu));

        assertThat(menuService.getMenusByBooth(1L, owner)).hasSize(1);
        verify(boothService).getOperableBooth(1L, owner);
    }

    @Test
    void getMenusByBooth_deniesOtherOperator() {
        AuthUser other = new AuthUser(2L, Role.OPERATOR);
        when(boothService.getOperableBooth(1L, other))
                .thenThrow(new AccessDeniedException("해당 부스에 접근할 권한이 없습니다."));

        assertThatThrownBy(() -> menuService.getMenusByBooth(1L, other))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getMenuById_requiresOperableBooth() {
        when(menuRepository.findById(10L)).thenReturn(Optional.of(menu));
        when(boothService.getOperableBooth(1L, owner)).thenReturn(booth);

        assertThat(menuService.getMenuById(10L, owner).id()).isEqualTo(10L);
        verify(boothService).getOperableBooth(1L, owner);
    }
}
