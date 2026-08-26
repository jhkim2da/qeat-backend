package com.qeat.service;

import com.qeat.dto.sejong.SejongLoginRequestDto;
import com.qeat.exception.LoginFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SejongAuthService {

    private static final Logger log = LoggerFactory.getLogger(SejongAuthService.class);

    private static final String PORTAL_LOGIN_URL =
            "https://portal.sejong.ac.kr/jsp/login/login_action.jsp";

    private static final String SSO_URL =
            "http://classic.sejong.ac.kr/_custom/sejong/sso/sso-return.jsp?returnUrl=https://classic.sejong.ac.kr/classic/index.do";

    public String getSsoToken(SejongLoginRequestDto loginRequestDto) {
        try {
            String formData = buildFormData(loginRequestDto);

            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .build();

            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(URI.create(PORTAL_LOGIN_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Referer", "https://portal.sejong.ac.kr/")
                    .header("Origin", "https://portal.sejong.ac.kr")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> loginResponse =
                    httpClient.send(loginRequest, HttpResponse.BodyHandlers.ofString());

            log.info("Sejong portal login responded with status {}", loginResponse.statusCode());

            String ssoToken = extractSsotokenFromHeaders(loginResponse.headers());

            if (ssoToken == null || ssoToken.isBlank()) {
                throw new LoginFailedException("학번 또는 비밀번호가 틀렸습니다.");
            }

            HttpRequest ssoRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SSO_URL))
                    .header("Cookie", "ssotoken=" + ssoToken + ";")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .GET()
                    .build();

            httpClient.send(ssoRequest, HttpResponse.BodyHandlers.ofString());

            return ssoToken;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LoginFailedException("로그인에 실패했습니다.");
        } catch (IOException e) {
            throw new LoginFailedException("로그인에 실패했습니다.");
        }
    }

    private String buildFormData(SejongLoginRequestDto loginRequestDto) {
        return "mainLogin=" + encode("N")
                + "&rtUrl=" + encode("library.sejong.ac.kr")
                + "&id=" + encode(loginRequestDto.getUserId())
                + "&password=" + encode(loginRequestDto.getPassword());
    }

    private String extractSsotokenFromHeaders(HttpHeaders headers) {
        List<String> cookies = headers.allValues("set-cookie");

        for (String cookieHeader : cookies) {
            String token = extractSsotoken(cookieHeader);
            if (token != null && !token.isBlank()) {
                return token;
            }
        }
        return null;
    }

    private String extractSsotoken(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return null;
        }

        Pattern pattern = Pattern.compile("ssotoken=([^;]+)");
        Matcher matcher = pattern.matcher(cookieHeader);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}