package com.qeat.service;

import com.qeat.domain.Role;
import com.qeat.domain.User;
import com.qeat.dto.sejong.SejongProfileResponseDto;
import com.qeat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final boolean bootstrapAdminEnabled;

    public UserService(
            UserRepository userRepository,
            @Value("${qeat.bootstrap-admin.enabled:false}") boolean bootstrapAdminEnabled
    ) {
        this.userRepository = userRepository;
        this.bootstrapAdminEnabled = bootstrapAdminEnabled;
    }

    @Transactional
    public User findOrSave(SejongProfileResponseDto profile) {
        String studentId = profile.studentId();
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("학번이 없습니다.");
        }

        String normalizedStudentId = studentId.trim();
        Optional<User> user = userRepository.findByStudentNumber(normalizedStudentId);
        if (user.isPresent()) {
            return user.get();
        }

        User newUser = User.create(
                normalizedStudentId,
                profile.name(),
                profile.major(),
                profile.gradeLevel(),
                Role.OPERATOR
        );
        return userRepository.save(newUser);
    }

    @Transactional
    public User bootstrapAdmin(Long userId) {
        if (!bootstrapAdminEnabled) {
            throw new AccessDeniedException("ADMIN 부트스트랩이 비활성화되어 있습니다.");
        }
        if (userRepository.existsByRole(Role.ADMIN)) {
            throw new IllegalStateException("이미 ADMIN 사용자가 존재합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.changeRole(Role.ADMIN);
        return user;
    }
}
