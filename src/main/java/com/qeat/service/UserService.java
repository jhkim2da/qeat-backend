package com.qeat.service;

import com.qeat.domain.Role;
import com.qeat.domain.User;
import com.qeat.dto.sejong.SejongProfileResponseDto;
import com.qeat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        if (userRepository.existsByRole(Role.ADMIN)) {
            throw new IllegalStateException("이미 ADMIN 사용자가 존재합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.changeRole(Role.ADMIN);
        return user;
    }
}
