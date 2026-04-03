package com.example.demo.Service;

import com.example.demo.domain.User;
import com.example.demo.dto.auth.LoginResponseDto;
import com.example.demo.dto.sejong.SejongLoginRequestDto;
import com.example.demo.dto.sejong.SejongProfileResponseDto;
import org.springframework.stereotype.Service;

@Service
public class SejongUserService {

    private final SejongAuthService sejongAuthService;
    private final SejongProfileService sejongProfileService;
    private final UserService userService;

    public SejongUserService(SejongAuthService sejongAuthService,
                             SejongProfileService sejongProfileService,
                             UserService userService) {
        this.sejongAuthService = sejongAuthService;
        this.sejongProfileService = sejongProfileService;
        this.userService = userService;
    }

    public LoginResponseDto login(SejongLoginRequestDto loginRequestDto) {
        String ssoToken = sejongAuthService.getSsoToken(loginRequestDto);
        SejongProfileResponseDto profile = sejongProfileService.fetchUserProfile(ssoToken);
        User user = userService.findOrSave(profile);
        return LoginResponseDto.from(user);
    }
}