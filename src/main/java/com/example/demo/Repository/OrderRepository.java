package com.example.demo.Repository;

import com.example.demo.domain.Order;
import com.example.demo.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBoothId(Long bootId);
    List<Order> findByBoothIdAndStatusAndCompletedAtBetween(Long bootId, Status status, LocalDateTime start, LocalDateTime end);
}
