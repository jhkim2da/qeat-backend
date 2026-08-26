package com.qeat.service;

import com.qeat.dto.sejong.SejongProfileResponseDto;
import com.qeat.exception.LoginFailedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class SejongProfileService {

    private static final Logger log = LoggerFactory.getLogger(SejongProfileService.class);

    private static final String PROFILE_URL =
            "https://classic.sejong.ac.kr/classic/reading/status.do";

    public SejongProfileResponseDto fetchUserProfile(String ssoToken) {
        try {
            if (ssoToken == null || ssoToken.isBlank()) {
                throw new LoginFailedException("로그인에 실패했습니다.");
            }

            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PROFILE_URL))
                    .header("Cookie", "ssotoken=" + ssoToken + ";")
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String html = response.body();

            log.info("Sejong profile responded with status {}", response.statusCode());

            if (html.contains("로그인") || html.contains("세종대학교 포털")) {
                throw new LoginFailedException("로그인에 실패했습니다.");
            }

            return parseProfileFromHtml(html);

        } catch (LoginFailedException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LoginFailedException("로그인에 실패했습니다.");
        } catch (IOException e) {
            throw new LoginFailedException("로그인에 실패했습니다.");
        }
    }

    private SejongProfileResponseDto parseProfileFromHtml(String html) {
        Document document = Jsoup.parse(html);

        String major = document.select("th:contains(학과명) + td").text().trim();
        String studentId = document.select("th:contains(학번) + td").text().trim();
        String name = document.select("th:contains(이름) + td").text().trim();
        String gradeLevel = document.select("th:contains(학년) + td").text().trim();

        if (major.isBlank() || studentId.isBlank() || name.isBlank() || gradeLevel.isBlank()) {
            throw new LoginFailedException("로그인에 실패했습니다.");
        }

        return new SejongProfileResponseDto(
                major,
                studentId,
                name,
                gradeLevel
        );
    }
}