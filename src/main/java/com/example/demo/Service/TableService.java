package com.example.demo.Service;

import com.example.demo.Repository.BoothTableRepository;
import com.example.demo.domain.Booth;
import com.example.demo.domain.BoothTable;
import com.example.demo.dto.auth.AuthUser;
import com.example.demo.global.security.TableTokenGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return boothTableRepository.findAllByBooth_IdAndActiveTrue(boothId);
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

        if (boothTableRepository.existsByBooth_IdAndTableNumber(boothId, tableNumber)) {
            throw new IllegalStateException("이미 존재하는 테이블 번호입니다.");
        }

        String token = createUniqueTableToken();

        BoothTable boothTable = new BoothTable(booth, tableNumber, token);
        String qrUrl = qrBaseUrl + "/qr/" + token;
        String qrImageUrl = qrCodeService.generateQrImage(qrUrl, token);
        boothTable.setQrImageUrl(qrImageUrl);

        return boothTableRepository.save(boothTable);
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
}
