package com.example.demo.Service;

import com.example.demo.Repository.BoothRepository;
import com.example.demo.domain.Booth;
import com.example.demo.dto.booth.BoothCreateRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class BoothService {
    private final BoothRepository boothRepository;
    public BoothService(BoothRepository boothRepository) {
        this.boothRepository = boothRepository;
    }

    @Transactional
    public Long createBooth(Long ownerId, BoothCreateRequest request) {
        Booth booth = Booth.create(
                request.name(),
                request.description(),
                ownerId
        );
        
        return boothRepository.save(booth).getId();
    }
}
