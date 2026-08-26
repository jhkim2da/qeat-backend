package com.qeat.service;

import com.qeat.domain.Booth;
import com.qeat.domain.BoothTable;
import com.qeat.dto.auth.AuthUser;
import com.qeat.global.security.TableTokenGenerator;
import com.qeat.repository.BoothTableRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TableService {

    private final BoothTableRepository boothTableRepository;
    private final BoothService boothService;
    private final QrCodeService qrCodeService;
    private final String qrBaseUrl;

    public TableService(
            BoothTableRepository boothTableRepository,
            BoothService boothService,
            QrCodeService qrCodeService,
            @Value("${qr.base-url}") String qrBaseUrl
    ) {
        this.boothTableRepository = boothTableRepository;
        this.boothService = boothService;
        this.qrCodeService = qrCodeService;
        this.qrBaseUrl = qrBaseUrl;
    }

    @Transactional(readOnly = true)
    public List<BoothTable> getTables(Long boothId, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);
        return boothTableRepository.findAllByBooth_IdAndActiveTrueOrderByTableNumberAsc(boothId);
    }

    @Transactional(readOnly = true)
    public List<BoothTable> getAllTables(Long boothId, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);
        return boothTableRepository.findAllByBooth_Id(boothId);
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

        return saveNewTable(booth, tableNumber);
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
            BoothTable savedTable = saveNewTable(booth, nextTableNumber);
            tables.add(savedTable);
            addedTables.add(savedTable);
        }

        return addedTables;
    }

    @Transactional
    public void deactivateTable(Long boothId, Long tableId, AuthUser authUser) {
        findTableInBooth(boothId, tableId, authUser).setActive(false);
    }

    @Transactional
    public void activateTable(Long boothId, Long tableId, AuthUser authUser) {
        findTableInBooth(boothId, tableId, authUser).setActive(true);
    }

    private BoothTable saveNewTable(Booth booth, int tableNumber) {
        String token = createUniqueTableToken();
        BoothTable boothTable = new BoothTable(booth, tableNumber, token);
        boothTable.setQrImageUrl(qrCodeService.generateQrImage(qrBaseUrl + "/qr/" + token, token));
        return boothTableRepository.save(boothTable);
    }

    private BoothTable findTableInBooth(Long boothId, Long tableId, AuthUser authUser) {
        boothService.getOperableBooth(boothId, authUser);
        BoothTable boothTable = boothTableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("해당 테이블이 없습니다."));
        if (!boothTable.getBooth().getId().equals(boothId)) {
            throw new AccessDeniedException("해당 부스의 테이블이 아닙니다.");
        }
        return boothTable;
    }

    private String createUniqueTableToken() {
        String token;
        do {
            token = TableTokenGenerator.generate();
        } while (boothTableRepository.existsByTableToken(token));
        return token;
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
}
