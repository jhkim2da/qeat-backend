package com.example.demo.Service;

import com.example.demo.dto.sejong.SejongLoginRequestDto;
import com.example.demo.dto.sejong.SejongProfileResponseDto;
import org.springframework.stereotype.Service;

@Service
public class SejongUserService {

    private final SejongAuthService sejongAuthService;
    private final SejongProfileService sejongProfileService;

    public SejongUserService(SejongAuthService sejongAuthService,
                             SejongProfileService sejongProfileService) {
        this.sejongAuthService = sejongAuthService;
        this.sejongProfileService = sejongProfileService;
    }

    public SejongProfileResponseDto login(SejongLoginRequestDto loginRequestDto) {
        String ssoToken = sejongAuthService.getSsoToken(loginRequestDto);
        return sejongProfileService.fetchUserProfile(ssoToken);
    }
}