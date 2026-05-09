package com.example.demo.repository;

import com.example.demo.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByBooth_Id(Long boothId);

    List<Menu> findByBooth_IdAndSoldOutFalse(Long boothId);

}
