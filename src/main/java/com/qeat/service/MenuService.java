package com.qeat.service;

import com.qeat.domain.Booth;
import com.qeat.domain.Menu;
import com.qeat.dto.auth.AuthUser;
import com.qeat.dto.menu.MenuCreateForm;
import com.qeat.dto.menu.MenuResponse;
import com.qeat.global.ImageUploader;
import com.qeat.repository.MenuRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<MenuResponse> getMenusByBooth(Long boothId) {
        return menuRepository.findByBooth_Id(boothId)
                .stream()
                .map(MenuResponse::from)
                .toList();
    }

    @Transactional
    public MenuResponse createMenu(Long boothId, AuthUser authUser, MenuCreateForm form) {
        Booth booth = boothService.getOperableBooth(boothId, authUser);
        String imageUrl = form.getImageUrl();
        if (form.getImage() != null && !form.getImage().isEmpty()) {
            imageUrl = imageUploader.upload(form.getImage());
        }

        Menu menu = new Menu(
                booth,
                form.getName(),
                form.getDescription(),
                form.getPrice(),
                imageUrl,
                form.getCategory()
        );
        return MenuResponse.from(menuRepository.save(menu));
    }

    @Transactional
    public void deleteMenu(Long boothId, Long menuId, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);
        Menu menu = getMenuInBooth(menuId, boothId);
        menuRepository.delete(menu);
    }

    @Transactional
    public void toggleSoldOut(Long boothId, Long menuId, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);
        Menu menu = getMenuInBooth(menuId, boothId);
        menu.toggleSoldOut();
    }

    @Transactional
    public MenuResponse updateMenu(Long boothId, Long menuId, AuthUser authUser, MenuCreateForm form) {
        boothService.getOperableBooth(boothId, authUser);
        Menu menu = getMenuInBooth(menuId, boothId);

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

    @Transactional(readOnly = true)
    public MenuResponse getMenuById(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다."));
        return MenuResponse.from(menu);
    }

    private Menu getMenuInBooth(Long menuId, Long boothId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("메뉴가 존재하지 않습니다."));
        if (!menu.getBooth().getId().equals(boothId)) {
            throw new AccessDeniedException("해당 부스의 메뉴가 아닙니다.");
        }
        return menu;
    }
}
