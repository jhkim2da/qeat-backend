package com.example.demo.Controller;

import com.example.demo.Service.MenuService;
import com.example.demo.dto.MenuCreateRequest;
import com.example.demo.dto.MenuResponse;
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
    public MenuResponse createMenu(@PathVariable Long boothId,  @RequestBody MenuCreateRequest request) {
        return menuService.createMenu(boothId, request);
    }

}
