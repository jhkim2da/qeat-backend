package com.example.demo.Repository;

import com.example.demo.domain.BoothTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoothTableRepository extends JpaRepository<BoothTable, Long> {
    boolean existsByTableToken(String tableToken);

    boolean existsByBooth_IdAndTableNumber(Long boothId, int tableNumber);

    Optional<BoothTable> findByTableTokenAndActiveTrue(String tableToken);

    List<BoothTable> findAllByBooth_IdAndActiveTrue(Long boothId);
}
