package com.example.demo.Service;

import com.example.demo.domain.User;
import com.example.demo.dto.auth.LoginResponseDto;
import com.example.demo.dto.sejong.SejongLoginRequestDto;
import com.example.demo.dto.sejong.SejongProfileResponseDto;
import com.example.demo.global.security.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public LoginResponseDto login(SejongLoginRequestDto loginRequestDto, HttpServletRequest request) {
        String ssoToken = sejongAuthService.getSsoToken(loginRequestDto);
        SejongProfileResponseDto profile = sejongProfileService.fetchUserProfile(ssoToken);
        User user = userService.findOrSave(profile);

        CustomUserPrincipal principal = new CustomUserPrincipal(
                user.getId(),
                user.getStudentNumber(),
                user.getName(),
                user.getRole()
        );

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        return LoginResponseDto.from(user);
    }
}