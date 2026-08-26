package com.qeat.global.redis;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PublicOrderRateLimitFilter extends OncePerRequestFilter {

    private static final Pattern PUBLIC_ORDER_PATH =
            Pattern.compile("^/api/public/tables/([^/]+)/orders$");

    private final PublicOrderRateLimiter rateLimiter;

    public PublicOrderRateLimitFilter(PublicOrderRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!isPublicOrderRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Matcher matcher = PUBLIC_ORDER_PATH.matcher(request.getRequestURI());
        if (!matcher.matches()) {
            filterChain.doFilter(request, response);
            return;
        }

        String tableToken = matcher.group(1);
        if (!rateLimiter.allow(tableToken, clientIp(request))) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"주문 요청이 너무 많습니다. 잠시 후 다시 시도해주세요.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicOrderRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && PUBLIC_ORDER_PATH.matcher(request.getRequestURI()).matches();
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
