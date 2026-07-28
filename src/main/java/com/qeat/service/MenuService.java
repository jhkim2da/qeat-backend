package com.qeat.service;

import com.qeat.repository.BoothRepository;
import com.qeat.repository.MenuRepository;
import com.qeat.domain.Booth;
import com.qeat.domain.Category;
import com.qeat.domain.Menu;
import com.qeat.dto.auth.AuthUser;
import com.qeat.dto.menu.MenuCreateForm;
import com.qeat.dto.menu.MenuResponse;
import com.qeat.global.ImageUploader;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final ImageUploader imageUploader;
    private final BoothService boothService;

    public MenuService(MenuRepository menuRepository, ImageUploader imageUploader, BoothService boothService) {
        this.menuRepository = menuRepository;
        this.imageUploader = imageUploader;
        this.boothService = boothService;
    }

    public List<MenuResponse> getMenusByBooth(Long boothId) {
        return menuRepository.findByBooth_Id(boothId)
                .stream()
                .map(MenuResponse::from) // 엔티티 → API 응답용 데이터로 바꾸는 역할
                .toList();
    }
    @Transactional
    public MenuResponse createMenu(Long boothId, AuthUser authUser, MenuCreateForm form) {

        Booth booth = boothService.getOperableBooth(boothId,  authUser);

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
                form.getCategory()
        );

        Menu savedMenu = menuRepository.save(menu);
        return MenuResponse.from(savedMenu);
    }

    @Transactional
    public void deleteMenu(Long boothId, Long menuId, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 메뉴가 존재하지 않습니다."));
        if (!menu.getBooth().getId().equals(boothId)) {
            throw new AccessDeniedException("해당 부스의 메뉴가 아닙니다.");
        }

        menuRepository.deleteById(menuId);
    }

    @Transactional
    public void toggleSoldOut(Long boothId, Long menuId,  AuthUser authUser) {
        Booth booth = boothService.getOperableBooth(boothId, authUser);
        Menu menu = menuRepository.findById(menuId).orElseThrow(()-> new IllegalArgumentException("품절 상태를 바꿀 메뉴가 존재하지 않습니다."));
        if (!menu.getBooth().getId().equals(booth.getId())) {
            throw new AccessDeniedException("해당 부스의 메뉴만 수정할 수 있습니다.");
        }
        if(menu.isSoldOut()) {
            menu.setSoldOut(false);
        } else {
            menu.setSoldOut(true);
        }
        menuRepository.save(menu);
    }

    @Transactional
    public MenuResponse updateMenu(Long boothId ,Long menuId, AuthUser authUser,MenuCreateForm form) {
        Booth booth = boothService.getOperableBooth(boothId, authUser);
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new IllegalArgumentException("수정을 할 메뉴가 존재하지 않습니다."));
        if (!menu.getBooth().getId().equals(booth.getId())) {
            throw new AccessDeniedException("해당 부스의 메뉴만 수정할 수 있습니다.");
        }
        String imageUrl = menu.getImageUrl();

        if (form.getImage() != null && !form.getImage().isEmpty()) {
            imageUrl = imageUploader.upload(form.getImage());
        } else if (form.getImageUrl() != null && !form.getImageUrl().isBlank()) {
            imageUrl = form.getImageUrl();
        }

        menu.update(
                form.getName(),
                form.getDescription(),
                form.getPrice(),
                imageUrl,
                form.getCategory()
        );

        return MenuResponse.from(menu);
    }

    public MenuResponse getMenuById(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new IllegalArgumentException("찾으려는 메뉴가 존재하지 않음"));
        return MenuResponse.from(menu);
    }
}
