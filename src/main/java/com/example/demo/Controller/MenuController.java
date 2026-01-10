package com.example.demo.Controller;

import com.example.demo.Service.MenuService;
import com.example.demo.dto.MenuResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
