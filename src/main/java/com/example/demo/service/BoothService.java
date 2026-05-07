package com.example.demo.service;

import com.example.demo.repository.BoothRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.domain.Booth;
import com.example.demo.domain.BoothStatus;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.dto.auth.AuthUser;
import com.example.demo.dto.booth.BoothCreateRequest;
import com.example.demo.dto.booth.BoothDetailResponse;
import com.example.demo.dto.booth.BoothMyResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;


@Service
public class BoothService {
    private final BoothRepository boothRepository;
    private final UserRepository userRepository;

    public BoothService(BoothRepository boothRepository, UserRepository userRepository) {
        this.boothRepository = boothRepository;
        this.userRepository = userRepository;
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

    @Transactional
    public void approveBooth(Long boothId, Long  userId) {
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new IllegalArgumentException("부스를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원가입이 되어있는 사용자만 접근 가능합니다."));
        if(user.getRole() == Role.ADMIN){
            booth.approve();
        } else{
            throw new AccessDeniedException("관리자만 접근 가능합니다.");
        }
    }

    @Transactional
    public void rejectBooth(Long boothId, Long userId) {
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new IllegalArgumentException("부스를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원가입이 되어있는 사용자만 접근 가능합니다."));
        if(user.getRole() == Role.ADMIN){
            booth.reject();
        } else{
            throw new AccessDeniedException("관리자만 접근 가능합니다.");
        }
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
    public Booth updateOperatingTime(Long boothId, AuthUser authUser, LocalTime openTime, LocalTime closeTime) {
        Booth booth = getOperableBooth(boothId, authUser);

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
    public Booth clearOperatingTime(Long boothId, AuthUser authUser) {
        Booth booth =  getOperableBooth(boothId, authUser);
        booth.clearOperatingTime();
        return booth;
    }

    public Booth getOperableBooth(Long boothId, AuthUser authUser) {
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new IllegalArgumentException("부스를 찾을 수 없습니다."));

        boolean isOwner = booth.getOwnerId().equals(authUser.userId());
        boolean isAdmin = authUser.role() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("해당 부스에 접근할 권한이 없습니다.");
        }

        if (booth.getBoothStatus() != BoothStatus.APPROVED) {
            throw new IllegalStateException("승인된 부스만 운영할 수 있습니다.");
        }

        return booth;
    }
}
