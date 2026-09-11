package com.qeat.controller;

import com.qeat.dto.menu.MenuCreateForm;
import com.qeat.dto.menu.MenuResponse;
import com.qeat.global.security.CustomUserPrincipal;
import com.qeat.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/api/booths/{boothId}/menus")
    public List<MenuResponse> getMenusByBooth(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return menuService.getMenusByBooth(boothId, principal.toAuthUser());
    }

    @PostMapping("/api/booths/{boothId}/menus")
    public MenuResponse createMenu(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @ModelAttribute MenuCreateForm form
    ) {
        return menuService.createMenu(boothId, principal.toAuthUser(), form);
    }

    @DeleteMapping("/api/booths/{boothId}/menus/{menuId}")
    public ResponseEntity<Void> deleteMenu(
            @PathVariable Long boothId,
            @PathVariable Long menuId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        menuService.deleteMenu(boothId, menuId, principal.toAuthUser());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/booths/{boothId}/menus/{menuId}/sold-out")
    public ResponseEntity<Void> toggleSoldOut(
            @PathVariable Long boothId,
            @PathVariable Long menuId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        menuService.toggleSoldOut(boothId, menuId, principal.toAuthUser());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/booths/{boothId}/menus/{menuId}")
    public MenuResponse updateMenu(
            @PathVariable Long boothId,
            @PathVariable Long menuId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @ModelAttribute MenuCreateForm form
    ) {
        return menuService.updateMenu(boothId, menuId, principal.toAuthUser(), form);
    }

    @GetMapping("/api/menus/{menuId}")
    public MenuResponse getMenu(
            @PathVariable Long menuId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return menuService.getMenuById(menuId, principal.toAuthUser());
    }
}
