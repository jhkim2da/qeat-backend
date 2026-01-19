package com.example.demo.Service;

import com.example.demo.Repository.BoothRepository;
import com.example.demo.Repository.MenuRepository;
import com.example.demo.domain.Booth;
import com.example.demo.domain.Menu;
import com.example.demo.dto.menu.MenuCreateRequest;
import com.example.demo.dto.menu.MenuResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final BoothRepository boothRepository;

    public MenuService(MenuRepository menuRepository, BoothRepository boothRepository) {
        this.menuRepository = menuRepository;
        this.boothRepository = boothRepository;
    }

    public List<MenuResponse> getMenusByBooth(Long boothId) {
        return menuRepository.findByBooth_IdAndSoldOutFalse(boothId)
                .stream()
                .map(MenuResponse::from) // 엔티티 → API 응답용 데이터로 바꾸는 역할
                .toList();
    }

    public MenuResponse createMenu(Long boothId, MenuCreateRequest request) {
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new IllegalArgumentException("부스가 존재하지 않음"));

        Menu menu = new Menu( // 도메인에서 정의한 생성틀에 맞게 인자만 넣어주었습니다.
                booth,
                request.name(),
                request.description(),
                request.price(),
                request.imageUrl(),
                request.category()
        );

        Menu savedMenu = menuRepository.save(menu);
        return MenuResponse.from(savedMenu);
    }
}
