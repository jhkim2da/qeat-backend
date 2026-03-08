package com.example.demo.Service;

import com.example.demo.Repository.BoothRepository;
import com.example.demo.Repository.MenuRepository;
import com.example.demo.domain.Booth;
import com.example.demo.domain.Category;
import com.example.demo.domain.Menu;
import com.example.demo.dto.menu.MenuCreateForm;
import com.example.demo.dto.menu.MenuResponse;
import com.example.demo.global.imageUploader;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final BoothRepository boothRepository;
    private final imageUploader imageUploader;

    public MenuService(MenuRepository menuRepository, BoothRepository boothRepository, imageUploader imageUploader) {
        this.menuRepository = menuRepository;
        this.boothRepository = boothRepository;
        this.imageUploader = imageUploader;
    }

    public List<MenuResponse> getMenusByBooth(Long boothId) {
        return menuRepository.findByBooth_IdAndSoldOutFalse(boothId)
                .stream()
                .map(MenuResponse::from) // 엔티티 → API 응답용 데이터로 바꾸는 역할
                .toList();
    }

    public MenuResponse createMenu(Long boothId, MenuCreateForm form) {
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new IllegalArgumentException("부스가 존재하지 않음"));

        String imageUrl = form.getImageUrl();

        if (form.getImage() != null && !form.getImage().isEmpty()) {
            imageUrl = imageUploader.upload(form.getImage());
        }
        Menu menu = new Menu( // 도메인에서 정의한 생성틀에 맞게 인자만 넣어주었습니다.
                booth,
                form.getName(),
                form.getDescription(),
                form.getPrice(),
                imageUrl,
                Category.valueOf(form.getCategory())
        );

        Menu savedMenu = menuRepository.save(menu);
        return MenuResponse.from(savedMenu);
    }

    @Transactional
    public void deleteMenu(Long menuId) {
        if(!menuRepository.existsById(menuId)) {
            throw new IllegalArgumentException("삭제할 메뉴가 존재하지 않습니다.");
        }
        menuRepository.deleteById(menuId);
    }

    @Transactional
    public void toggleSoldOut(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(()-> new IllegalArgumentException("품절 상태를 바꿀 메뉴가 존재하지 않습니다."));
        if(menu.isSoldOut()) {
            menu.setSoldOut(false);
        } else {
            menu.setSoldOut(true);
        }
        menuRepository.save(menu);
    }

    @Transactional
    public MenuResponse updateMenu(Long menuId, MenuCreateForm form) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new IllegalArgumentException("수정을 할 메뉴가 존재하지 않습니다."));

        String imageUrl = menu.getImageUrl();

        if (form.getImage() != null && !form.getImage().isEmpty()) {
            imageUrl = imageUploader.upload(form.getImage());
        } else if (form.getImageUrl() != null) {
            imageUrl = form.getImageUrl();
        }

        menu.update(
                form.getName(),
                form.getDescription(),
                form.getPrice(),
                imageUrl,
                Category.valueOf(form.getCategory())
        );

        return MenuResponse.from(menu);
    }

    public MenuResponse getMenuById(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new IllegalArgumentException("찾으려는 메뉴가 존재하지 않음"));
        return MenuResponse.from(menu);
    }
}
