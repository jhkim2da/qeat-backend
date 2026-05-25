package com.example.demo.repository;

import com.example.demo.domain.Booth;
import com.example.demo.domain.BoothStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BoothRepository extends JpaRepository<Booth, Long> {
    List<Booth> findAllByOwnerId(Long ownerId);
    List<Booth> findAllByOwnerIdAndBoothStatus(Long ownerId, BoothStatus boothStatus);
    List<Booth> findAllByBoothStatus(BoothStatus boothStatus);
    Optional<Booth> findByIdAndOwnerId(Long id, Long ownerId);
}
