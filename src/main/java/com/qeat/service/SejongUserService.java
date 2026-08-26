package com.qeat.service;

import com.qeat.domain.User;
import com.qeat.dto.auth.LoginResponseDto;
import com.qeat.dto.sejong.SejongLoginRequestDto;
import com.qeat.dto.sejong.SejongProfileResponseDto;
import com.qeat.global.security.CustomUserPrincipal;
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