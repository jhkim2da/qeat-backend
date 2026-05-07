package com.example.demo.repository;

import com.example.demo.domain.BoothTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoothTableRepository extends JpaRepository<BoothTable, Long> {
    boolean existsByTableToken(String tableToken);

    boolean existsByBooth_IdAndTableNumber(Long boothId, int tableNumber);

    Optional<BoothTable> findByBooth_IdAndTableNumber(Long boothId, int tableNumber);

    Optional<BoothTable> findByTableTokenAndActiveTrue(String tableToken);

    List<BoothTable> findAllByBooth_Id(Long boothId);

    List<BoothTable> findAllByBooth_IdOrderByTableNumberAsc(Long boothId);

    List<BoothTable> findAllByBooth_IdAndActiveTrueOrderByTableNumberAsc(Long boothId);
}
