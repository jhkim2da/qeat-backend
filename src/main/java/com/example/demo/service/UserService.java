package com.example.demo.service;

import com.example.demo.repository.UserRepository;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.dto.sejong.SejongProfileResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrSave(SejongProfileResponseDto sejongProfileResponseDto) {
        String studentId = sejongProfileResponseDto.getStudentId();

        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("학번이 없습니다.");
        }

        studentId = studentId.trim();

        Optional<User> user = userRepository.findByStudentNumber(studentId);

        if (user.isPresent()) {
            return user.get();
        }

        User newUser = User.create(
                studentId,
                sejongProfileResponseDto.getName(),
                sejongProfileResponseDto.getMajor(),
                sejongProfileResponseDto.getGradeLevel(),
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
