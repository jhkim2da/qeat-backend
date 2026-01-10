package com.example.demo.Service;

import com.example.demo.Repository.MenuRepository;
import com.example.demo.dto.MenuResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuResponse> getMenusByBooth(Long boothId) {
        return menuRepository.findByBooth_IdAndSoldOutFalse(boothId)
                .stream()
                .map(MenuResponse::from) // 엔티티 → API 응답용 데이터로 바꾸는 역할
                .toList();
    }
}
