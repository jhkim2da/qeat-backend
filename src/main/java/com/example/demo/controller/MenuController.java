package com.example.demo.controller;

import com.example.demo.Service.MenuService;
import com.example.demo.dto.menu.MenuCreateForm;
import com.example.demo.dto.menu.MenuResponse;
import org.springframework.http.ResponseEntity;
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
    public MenuResponse createMenu(@PathVariable Long boothId,  @ModelAttribute MenuCreateForm form) {
        return menuService.createMenu(boothId, form);
    }

    @DeleteMapping("/api/menus/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok().build();
    }
}
