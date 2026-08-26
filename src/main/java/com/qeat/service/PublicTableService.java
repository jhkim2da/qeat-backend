package com.qeat.service;

import com.qeat.domain.BoothTable;
import com.qeat.dto.menu.MenuResponse;
import com.qeat.dto.table.PublicTableMenuResponse;
import com.qeat.global.redis.PublicMenuCache;
import com.qeat.repository.BoothTableRepository;
import com.qeat.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublicTableService {

    private final BoothTableRepository boothTableRepository;
    private final MenuRepository menuRepository;
    private final PublicMenuCache publicMenuCache;

    public PublicTableService(
            BoothTableRepository boothTableRepository,
            MenuRepository menuRepository,
            PublicMenuCache publicMenuCache
    ) {
        this.boothTableRepository = boothTableRepository;
        this.menuRepository = menuRepository;
        this.publicMenuCache = publicMenuCache;
    }

    @Transactional(readOnly = true)
    public BoothTable findActiveTable(String tableToken) {
        return boothTableRepository.findByTableTokenAndActiveTrue(tableToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 테이블 QR입니다."));
    }

    @Transactional(readOnly = true)
    public PublicTableMenuResponse getTableMenu(String tableToken) {
        BoothTable table = findActiveTable(tableToken);
        return PublicTableMenuResponse.from(table, getPublicMenus(table.getBooth().getId()));
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getPublicMenus(Long boothId) {
        List<MenuResponse> cached = publicMenuCache.get(boothId);
        if (cached != null) {
            return cached;
        }

        List<MenuResponse> menus = menuRepository.findByBooth_Id(boothId)
                .stream()
                .map(MenuResponse::from)
                .toList();
        publicMenuCache.put(boothId, menus);
        return menus;
    }
}
