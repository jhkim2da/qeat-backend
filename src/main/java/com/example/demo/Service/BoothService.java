package com.example.demo.Service;

import com.example.demo.Repository.BoothRepository;
import com.example.demo.Repository.MenuRepository;
import com.example.demo.domain.Booth;
import com.example.demo.dto.booth.BoothCreateRequest;
import com.example.demo.dto.booth.BoothDetailResponse;
import com.example.demo.dto.booth.BoothMyResponse;
import com.example.demo.dto.menu.MenuResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;


@Service
public class BoothService {
    private final BoothRepository boothRepository;

    public BoothService(BoothRepository boothRepository) {
        this.boothRepository = boothRepository;
    }

    @Transactional
    public Long createBooth(Long ownerId, BoothCreateRequest request) {
        String normalizedAccountNumber = request.accountNumber()
                .replaceAll("[^0-9]", "");

        Booth booth = Booth.create(
                request.name(),
                request.description(),
                ownerId,
                request.bank(),
                normalizedAccountNumber
        );
        return boothRepository.save(booth).getId();
    }

    @Transactional(readOnly = true)
    public List<BoothMyResponse> getMyBooth(Long ownerid) {
        return boothRepository.findAllByOwnerId(ownerid)
                .stream()
                .map(BoothMyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BoothDetailResponse getMyBoothDetail(Long ownerId, Long boothId) {
        Booth booth = boothRepository.findByIdAndOwnerId(boothId, ownerId)
                .orElseThrow(() -> new RuntimeException("해당 부스를 찾을 수 없습니다."));

        return BoothDetailResponse.from(booth);
    }

    @Transactional
    public Booth updateBooth(Long ownerId, Long boothId, BoothCreateRequest request) {

        String normalizedAccountNumber = request.accountNumber()
                .replaceAll("[^0-9]", "");

        Booth booth = boothRepository.findByIdAndOwnerId(boothId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부스를 찾을 수 없습니다."));

        booth.update(
                request.name(),
                request.bank(),
                normalizedAccountNumber,
                request.description()
        );

        return booth;
    }
    @Transactional
    public Boolean updateOpenStatus(Long ownerId, Long boothId, boolean open) {
        Booth booth = boothRepository.findByIdAndOwnerId(boothId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("부스를 찾을 수 없습니다."));

        booth.changeOpenStatus(open);
        return booth.isOpen();
    }

    @Transactional
    public Booth updateOperatingTime(Long ownerId, Long boothId, LocalTime openTime, LocalTime closeTime) {
        Booth booth = boothRepository.findByIdAndOwnerId(boothId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("부스를 찾을 수 없습니다."));

        if (openTime == null || closeTime == null) {
            throw new IllegalArgumentException("시작 시간과 종료 시간은 모두 입력해야 합니다.");
        }

        if (!openTime.isBefore(closeTime)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야 합니다.");
        }

        booth.changeOperatingTime(openTime, closeTime);
        return booth;
    }

    @Transactional
    public Booth clearOperatingTime(Long ownerId, Long boothId) {
        Booth booth = boothRepository.findByIdAndOwnerId(boothId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("부스를 찾을 수 없습니다."));

        booth.clearOperatingTime();
        return booth;
    }
}
