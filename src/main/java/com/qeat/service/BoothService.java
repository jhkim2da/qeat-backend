package com.qeat.service;

import com.qeat.repository.BoothRepository;
import com.qeat.repository.UserRepository;
import com.qeat.domain.Booth;
import com.qeat.domain.BoothStatus;
import com.qeat.domain.Role;
import com.qeat.domain.User;
import com.qeat.dto.auth.AuthUser;
import com.qeat.dto.booth.BoothCreateRequest;
import com.qeat.dto.booth.BoothDetailResponse;
import com.qeat.dto.booth.BoothMyResponse;
import com.qeat.dto.booth.BoothOperatorDetailResponse;
import com.qeat.dto.booth.BoothOperatorResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Map;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


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
                .filter(booth -> booth.getBoothStatus() != BoothStatus.DELETED)
                .map(BoothMyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BoothMyResponse> getMyApprovedBooths(Long ownerId) {
        return boothRepository.findAllByOwnerIdAndBoothStatus(ownerId, BoothStatus.APPROVED)
                .stream()
                .map(BoothMyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BoothMyResponse> getPendingBooths(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원가입이 되어있는 사용자만 접근 가능합니다."));

        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("관리자만 접근 가능합니다.");
        }

        return boothRepository.findAllByBoothStatus(BoothStatus.PENDING)
                .stream()
                .map(BoothMyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BoothOperatorResponse> getBoothOperators(Long userId) {
        User admin = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원가입이 되어있는 사용자만 접근 가능합니다."));

        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("관리자만 접근 가능합니다.");
        }

        Map<Long, Long> boothCountByOwnerId = boothRepository.findAllByBoothStatus(BoothStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(Booth::getOwnerId, Collectors.counting()));

        Map<Long, User> usersById = userRepository.findAllById(boothCountByOwnerId.keySet())
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return boothCountByOwnerId.entrySet()
                .stream()
                .map(entry -> {
                    User operator = usersById.get(entry.getKey());
                    if (operator == null) {
                        return null;
                    }

                    return new BoothOperatorResponse(
                            operator.getId(),
                            operator.getName(),
                            operator.getStudentNumber(),
                            operator.getMajor(),
                            entry.getValue()
                    );
                })
                .filter(response -> response != null)
                .toList();
    }

    @Transactional(readOnly = true)
    public BoothOperatorDetailResponse getBoothOperatorDetail(Long operatorId, Long adminId) {
        validateAdmin(adminId);

        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<BoothMyResponse> booths = boothRepository.findAllByOwnerId(operatorId)
                .stream()
                .map(BoothMyResponse::from)
                .toList();

        if (booths.isEmpty()) {
            throw new IllegalArgumentException("해당 사용자가 만든 부스가 없습니다.");
        }

        return new BoothOperatorDetailResponse(
                operator.getId(),
                operator.getName(),
                operator.getGrade(),
                operator.getStudentNumber(),
                operator.getMajor(),
                booths
        );
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
    public void deleteBooth(Long boothId, AuthUser authUser) {
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new IllegalArgumentException("부스를 찾을 수 없습니다."));

        boolean isOwner = booth.getOwnerId().equals(authUser.userId());
        boolean isAdmin = authUser.role() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("해당 부스를 삭제할 권한이 없습니다.");
        }

        if (booth.getBoothStatus() == BoothStatus.DELETED) {
            throw new IllegalStateException("이미 삭제된 부스입니다.");
        }

        booth.delete();
    }

    @Transactional
    public Booth suspendBooth(Long boothId, Long adminId) {
        validateAdmin(adminId);

        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new IllegalArgumentException("부스를 찾을 수 없습니다."));

        if (booth.getBoothStatus() == BoothStatus.DELETED) {
            throw new IllegalStateException("삭제된 부스는 중지할 수 없습니다.");
        }

        if (booth.getBoothStatus() == BoothStatus.SUSPENDED) {
            throw new IllegalStateException("이미 중지된 부스입니다.");
        }

        booth.suspend();
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

    private void validateAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원가입이 되어있는 사용자만 접근 가능합니다."));

        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("관리자만 접근 가능합니다.");
        }
    }
}
