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

import java.util.List;


@Service
public class BoothService {
    private final BoothRepository boothRepository;
    private final MenuRepository menuRepository;

    public BoothService(BoothRepository boothRepository, MenuRepository menuRepository) {
        this.boothRepository = boothRepository;
        this.menuRepository = menuRepository;
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
}
