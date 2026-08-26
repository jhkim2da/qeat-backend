package com.qeat.controller;

import com.qeat.domain.BoothTable;
import com.qeat.dto.table.BoothTableBulkCreateRequest;
import com.qeat.dto.table.BoothTableCreateRequest;
import com.qeat.dto.table.BoothTableResponse;
import com.qeat.global.security.CustomUserPrincipal;
import com.qeat.service.TableService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/booths/{boothId}/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping
    public BoothTableResponse addTable(
            @PathVariable Long boothId,
            @Valid @RequestBody BoothTableCreateRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        BoothTable boothTable = tableService.addTable(boothId, request.tableNumber(), principal.toAuthUser());
        return BoothTableResponse.from(boothTable);
    }

    @PostMapping("/bulk")
    public List<BoothTableResponse> addTables(
            @PathVariable Long boothId,
            @Valid @RequestBody BoothTableBulkCreateRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return tableService.addTables(boothId, request.count(), principal.toAuthUser())
                .stream()
                .map(BoothTableResponse::from)
                .toList();
    }

    @GetMapping
    public List<BoothTableResponse> getTables(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return tableService.getTables(boothId, principal.toAuthUser())
                .stream()
                .map(BoothTableResponse::from)
                .toList();
    }

    @GetMapping("/all")
    public List<BoothTableResponse> getAllTables(
            @PathVariable Long boothId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return tableService.getAllTables(boothId, principal.toAuthUser())
                .stream()
                .map(BoothTableResponse::from)
                .toList();
    }

    @PatchMapping("/{tableId}/deactivate")
    public String deactivateTable(
            @PathVariable Long boothId,
            @PathVariable Long tableId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        tableService.deactivateTable(boothId, tableId, principal.toAuthUser());
        return "테이블이 비활성화되었습니다.";
    }

    @PatchMapping("/{tableId}/activate")
    public String activateTable(
            @PathVariable Long boothId,
            @PathVariable Long tableId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        tableService.activateTable(boothId, tableId, principal.toAuthUser());
        return "테이블이 활성화되었습니다.";
    }
}
