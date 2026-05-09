package com.example.demo.service;

import com.example.demo.repository.BoothTableRepository;
import com.example.demo.domain.Booth;
import com.example.demo.domain.BoothTable;
import com.example.demo.dto.auth.AuthUser;
import com.example.demo.global.security.TableTokenGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TableService {
    BoothTableRepository boothTableRepository;
    BoothService boothService;
    QrCodeService qrCodeService;
    private final String qrBaseUrl;

    public TableService(BoothTableRepository boothTableRepository,
                        BoothService boothService,
                        QrCodeService qrCodeService,
                        @Value("${qr.base-url}") String qrBaseUrl) {
        this.boothTableRepository = boothTableRepository;
        this.boothService = boothService;
        this.qrCodeService = qrCodeService;
        this.qrBaseUrl = qrBaseUrl;
    }
    public List<BoothTable> getTables(Long boothId, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);
        return boothTableRepository.findAllByBooth_IdAndActiveTrueOrderByTableNumberAsc(boothId);
    }

    public List<BoothTable> getAllTables(Long boothId, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);
        return boothTableRepository.findAllByBooth_Id(boothId);
    }

    private String createUniqueTableToken() {
        String token;
        do {
            token = TableTokenGenerator.generate();
        } while (boothTableRepository.existsByTableToken(token));
        return token;
    }

    @Transactional
    public BoothTable addTable(Long boothId, int tableNumber, AuthUser authUser) {
        Booth booth = boothService.getOperableBooth(boothId, authUser);

        BoothTable existingTable = boothTableRepository.findByBooth_IdAndTableNumber(boothId, tableNumber)
                .orElse(null);

        if (existingTable != null) {
            if (existingTable.isActive()) {
                throw new IllegalStateException("이미 존재하는 테이블 번호입니다.");
            }

            existingTable.setActive(true);
            return existingTable;
        }

        String token = createUniqueTableToken();

        BoothTable boothTable = new BoothTable(booth, tableNumber, token);
        String qrUrl = qrBaseUrl + "/qr/" + token;
        String qrImageUrl = qrCodeService.generateQrImage(qrUrl, token);
        boothTable.setQrImageUrl(qrImageUrl);

        return boothTableRepository.save(boothTable);
    }

    @Transactional
    public List<BoothTable> addTables(Long boothId, int count, AuthUser authUser) {
        Booth booth = boothService.getOperableBooth(boothId, authUser);
        List<BoothTable> tables = boothTableRepository.findAllByBooth_IdOrderByTableNumberAsc(boothId);
        List<BoothTable> addedTables = new ArrayList<>();

        List<BoothTable> inactiveTables = tables.stream()
                .filter(table -> !table.isActive())
                .sorted(Comparator.comparingInt(BoothTable::getTableNumber))
                .toList();

        for (BoothTable table : inactiveTables) {
            if (addedTables.size() == count) {
                return addedTables;
            }

            table.setActive(true);
            addedTables.add(table);
        }

        while (addedTables.size() < count) {
            int nextTableNumber = findNextAvailableTableNumber(tables);
            String token = createUniqueTableToken();
            BoothTable boothTable = new BoothTable(booth, nextTableNumber, token);
            String qrUrl = qrBaseUrl + "/qr/" + token;
            String qrImageUrl = qrCodeService.generateQrImage(qrUrl, token);
            boothTable.setQrImageUrl(qrImageUrl);

            BoothTable savedTable = boothTableRepository.save(boothTable);
            tables.add(savedTable);
            addedTables.add(savedTable);
        }

        return addedTables;
    }

    private int findNextAvailableTableNumber(List<BoothTable> tables) {
        Set<Integer> tableNumbers = new HashSet<>();
        for (BoothTable table : tables) {
            tableNumbers.add(table.getTableNumber());
        }

        int tableNumber = 1;
        while (tableNumbers.contains(tableNumber)) {
            tableNumber++;
        }
        return tableNumber;
    }

    @Transactional
    public void deactivateTable(Long boothId, Long tableId, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);

        BoothTable boothTable = boothTableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("해당 테이블이 없습니다."));

        if (!boothTable.getBooth().getId().equals(boothId)) {
            throw new AccessDeniedException("해당 부스의 테이블이 아닙니다.");
        }

        boothTable.setActive(false);
    }

    @Transactional
    public void activateTable(Long boothId, Long tableId, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);

        BoothTable boothTable = boothTableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("해당 테이블이 없습니다."));

        if (!boothTable.getBooth().getId().equals(boothId)) {
            throw new AccessDeniedException("해당 부스의 테이블이 아닙니다.");
        }

        boothTable.setActive(true);
    }
}
