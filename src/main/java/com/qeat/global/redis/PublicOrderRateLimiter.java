package com.qeat.global.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class PublicOrderRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(PublicOrderRateLimiter.class);

    private final StringRedisTemplate redis;
    private final int maxRequests;
    private final Duration window;

    public PublicOrderRateLimiter(
            StringRedisTemplate redis,
            @Value("${qeat.order.rate-limit.max:20}") int maxRequests,
            @Value("${qeat.order.rate-limit.window:1m}") Duration window
    ) {
        this.redis = redis;
        this.maxRequests = maxRequests;
        this.window = window;
    }

    public boolean allow(String tableToken, String clientIp) {
        String key = key(tableToken, clientIp);
        try {
            Long count = redis.opsForValue().increment(key);
            if (count != null && count == 1L) {
                redis.expire(key, window);
            }
            return count == null || count <= maxRequests;
        } catch (Exception e) {
            log.warn("Failed to check public order rate limit for table {}", tableToken, e);
            return true;
        }
    }

    String key(String tableToken, String clientIp) {
        return "qeat:rate:order:" + tableToken + ":" + clientIp;
    }
}
