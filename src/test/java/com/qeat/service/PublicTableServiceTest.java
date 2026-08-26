package com.qeat.service;

import com.qeat.domain.Bank;
import com.qeat.domain.Booth;
import com.qeat.domain.BoothTable;
import com.qeat.domain.Category;
import com.qeat.domain.Menu;
import com.qeat.dto.menu.MenuResponse;
import com.qeat.global.redis.PublicMenuCache;
import com.qeat.repository.BoothTableRepository;
import com.qeat.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicTableServiceTest {

    @Mock
    private BoothTableRepository boothTableRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PublicMenuCache publicMenuCache;

    @InjectMocks
    private PublicTableService publicTableService;

    private Booth booth;

    @BeforeEach
    void setUp() {
        booth = Booth.create("축제부스", "설명", 1L, Bank.KB, "1234567890");
        booth.approve();
        ReflectionTestUtils.setField(booth, "id", 1L);
    }

    @Test
    void getPublicMenus_returnsCachedMenusWithoutDb() {
        List<MenuResponse> cached = List.of(
                new MenuResponse(1L, "떡볶이", "설명", 4000, Category.MAIN_FOOD, "/uploads/a.png", false)
        );
        when(publicMenuCache.get(1L)).thenReturn(cached);

        List<MenuResponse> menus = publicTableService.getPublicMenus(1L);

        assertThat(menus).isEqualTo(cached);
        verify(menuRepository, never()).findByBooth_Id(1L);
    }

    @Test
    void getPublicMenus_loadsFromDbAndFillsCacheOnMiss() {
        when(publicMenuCache.get(1L)).thenReturn(null);
        Menu menu = new Menu(booth, "떡볶이", "설명", 4000, "/uploads/a.png", Category.MAIN_FOOD);
        ReflectionTestUtils.setField(menu, "id", 10L);
        when(menuRepository.findByBooth_Id(1L)).thenReturn(List.of(menu));

        List<MenuResponse> menus = publicTableService.getPublicMenus(1L);

        assertThat(menus).hasSize(1);
        assertThat(menus.getFirst().name()).isEqualTo("떡볶이");
        verify(publicMenuCache).put(1L, menus);
    }

    @Test
    void getTableMenu_usesLiveBoothStatusWithCachedMenus() {
        BoothTable table = new BoothTable(booth, 1, "table-token");
        ReflectionTestUtils.setField(table, "id", 3L);
        when(boothTableRepository.findByTableTokenAndActiveTrue("table-token")).thenReturn(Optional.of(table));
        when(publicMenuCache.get(1L)).thenReturn(List.of());

        var response = publicTableService.getTableMenu("table-token");

        assertThat(response.canOrder()).isTrue();
        assertThat(response.boothId()).isEqualTo(1L);
        assertThat(response.tableToken()).isEqualTo("table-token");
    }
}
