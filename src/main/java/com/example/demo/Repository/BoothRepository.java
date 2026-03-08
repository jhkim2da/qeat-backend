package com.example.demo.Repository;

import com.example.demo.domain.Booth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BoothRepository extends JpaRepository<Booth, Long> {
    List<Booth> findAllByOwnerId(Long ownerId);
    Optional<Booth> findByIdAndOwnerId(Long ownerId, Long boothId);
}
