package com.example.demo.repository;

import com.example.demo.domain.User;
import com.example.demo.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByStudentNumber(String studentNumber);

    boolean existsByRole(Role role);
}
