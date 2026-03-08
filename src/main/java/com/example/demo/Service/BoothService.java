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
        Booth booth = Booth.create(
                request.name(),
                request.description(),
                ownerId
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

    public BoothDetailResponse getMyBoothDetail(Long ownerId, Long boothId) {
        Booth booth = boothRepository.findByIdAndOwnerId(ownerId, boothId)
                .orElseThrow(() -> new IllegalArgumentException("부스 없음"));
        List<MenuResponse> menus = menuRepository.findByBooth_Id(boothId)
                .stream()
                .map(MenuResponse::from)
                .toList();
        return BoothDetailResponse.from(booth, menus);
    }
}
