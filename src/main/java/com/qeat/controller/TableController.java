package com.qeat.controller;

import com.qeat.service.TableService;
import com.qeat.domain.BoothTable;
import com.qeat.dto.auth.AuthUser;
import com.qeat.dto.table.BoothTableBulkCreateRequest;
import com.qeat.dto.table.BoothTableCreateRequest;
import com.qeat.dto.table.BoothTableResponse;
import com.qeat.global.security.CustomUserPrincipal;
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

        @PostMapping("/bulk")
        public List<BoothTableResponse> addTables(@PathVariable Long boothId,
                                                  @Valid @RequestBody BoothTableBulkCreateRequest request,
                                                  @AuthenticationPrincipal CustomUserPrincipal principal) {
            AuthUser authUser = new AuthUser(principal.getId(), principal.getRole());
            return tableService.addTables(boothId, request.count(), authUser)
                    .stream()
                    .map(BoothTableResponse::from)
                    .toList();
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

        @GetMapping("/all")
        public List<BoothTableResponse> getAllTables(@PathVariable Long boothId,
                                                     @AuthenticationPrincipal CustomUserPrincipal principal) {
            AuthUser authUser = new AuthUser(principal.getId(), principal.getRole());
            return tableService.getAllTables(boothId, authUser)
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

        @PatchMapping("/{tableId}/activate")
        public String activateTable(@PathVariable Long boothId,
                                    @PathVariable Long tableId,
                                    @AuthenticationPrincipal CustomUserPrincipal principal) {
            AuthUser authUser = new AuthUser(principal.getId(), principal.getRole());
            tableService.activateTable(boothId, tableId, authUser);
            return "테이블이 활성화되었습니다.";
        }
}
