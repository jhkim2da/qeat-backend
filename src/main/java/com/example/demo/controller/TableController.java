package com.example.demo.controller;

import com.example.demo.Service.TableService;
import com.example.demo.domain.BoothTable;
import com.example.demo.dto.auth.AuthUser;
import com.example.demo.dto.table.BoothTableCreateRequest;
import com.example.demo.dto.table.BoothTableResponse;
import com.example.demo.global.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booths/{boothId}/tables")
public class TableController {

        private final TableService tableService;

        public TableController(TableService tableService) {
            this.tableService = tableService;
        }

        @PostMapping
        public BoothTableResponse addTable(@PathVariable Long boothId,
                                           @Valid @RequestBody BoothTableCreateRequest request,
                                           @AuthenticationPrincipal CustomUserPrincipal principal) {
            AuthUser authUser = new AuthUser(principal.getId(), principal.getRole());
            BoothTable boothTable = tableService.addTable(boothId, request.tableNumber(), authUser);
            return BoothTableResponse.from(boothTable);
        }

        @GetMapping
        public List<BoothTableResponse> getTables(@PathVariable Long boothId,
                                                  @AuthenticationPrincipal CustomUserPrincipal principal) {
            AuthUser authUser = new AuthUser(principal.getId(), principal.getRole());
            return tableService.getTables(boothId, authUser)
                    .stream()
                    .map(BoothTableResponse::from)
                    .toList();
        }

        @PatchMapping("/{tableId}/deactivate")
        public String deactivateTable(@PathVariable Long boothId,
                                      @PathVariable Long tableId,
                                      @AuthenticationPrincipal CustomUserPrincipal principal) {
            AuthUser authUser = new AuthUser(principal.getId(), principal.getRole());
            tableService.deactivateTable(boothId, tableId, authUser);
            return "테이블이 비활성화되었습니다.";
        }
}
