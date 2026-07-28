package com.qeat.repository;

import com.qeat.domain.User;
import com.qeat.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByStudentNumber(String studentNumber);

    boolean existsByRole(Role role);
}
