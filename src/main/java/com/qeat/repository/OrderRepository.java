package com.qeat.repository;

import com.qeat.domain.Order;
import com.qeat.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBoothId(Long bootId);
    List<Order> findByBoothIdOrderByCreatedAtDescIdDesc(Long boothId);
    List<Order> findByBoothIdAndStatusAndCompletedAtBetween(Long bootId, Status status, LocalDateTime start, LocalDateTime end);
}
