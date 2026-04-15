package com.example.demo.controller;

import com.example.demo.Service.MenuService;
import com.example.demo.dto.auth.AuthUser;
import com.example.demo.dto.menu.MenuCreateForm;
import com.example.demo.dto.menu.MenuResponse;
import com.example.demo.global.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MenuController {
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/api/booths/{boothId}/menus")
    public List<MenuResponse> getMenusByBooth(@PathVariable Long boothId) {
        return menuService.getMenusByBooth(boothId);
    }

    @PostMapping("/api/booths/{boothId}/menus")
    public MenuResponse createMenu(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails ,
            @Valid @ModelAttribute MenuCreateForm form) {
        AuthUser authUser = new AuthUser(userDetails.getId(), userDetails.getRole());
        return menuService.createMenu(boothId, authUser ,form);
    }

    @DeleteMapping("/api/booths/{boothId}/menus/{menuId}")
    public ResponseEntity<Void> deleteMenu(
            @PathVariable Long boothId,
            @PathVariable Long menuId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails) {
        AuthUser authUser = new AuthUser(userDetails.getId(), userDetails.getRole());
        menuService.deleteMenu(boothId, menuId, authUser);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/booths/{boothId}/menus/{menuId}/sold-out")
    public ResponseEntity<Void> toggleSoldOut(
            @PathVariable Long boothId,
            @PathVariable Long menuId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails) {
        AuthUser authUser = new AuthUser(userDetails.getId(), userDetails.getRole());
        menuService.toggleSoldOut(boothId, menuId,authUser);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/booths/{boothId}/menus/{menuId}")
    public  MenuResponse updateMenu(
            @PathVariable Long boothId,
            @PathVariable Long menuId,
            @AuthenticationPrincipal CustomUserPrincipal userDetails,
            @Valid @ModelAttribute MenuCreateForm form) {
        AuthUser authUser = new AuthUser(userDetails.getId(), userDetails.getRole());
        return menuService.updateMenu( boothId, menuId, authUser, form);
    }

    @GetMapping("/api/menus/{menuId}")
    public MenuResponse getMenu(@PathVariable Long menuId) {
        return menuService.getMenuById(menuId);
    }
}
