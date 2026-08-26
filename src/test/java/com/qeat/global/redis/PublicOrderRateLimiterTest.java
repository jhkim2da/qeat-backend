package com.qeat.global.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicOrderRateLimiterTest {

    @Mock
    private StringRedisTemplate redis;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private PublicOrderRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        when(redis.opsForValue()).thenReturn(valueOperations);
        rateLimiter = new PublicOrderRateLimiter(redis, 20, Duration.ofMinutes(1));
    }

    @Test
    void firstRequestSetsExpiryAndAllows() {
        String key = "qeat:rate:order:table-token:127.0.0.1";
        when(valueOperations.increment(key)).thenReturn(1L);

        boolean allowed = rateLimiter.allow("table-token", "127.0.0.1");

        assertThat(allowed).isTrue();
        verify(redis).expire(eq(key), eq(Duration.ofMinutes(1)));
    }

    @Test
    void requestOverLimitIsDenied() {
        when(valueOperations.increment("qeat:rate:order:table-token:127.0.0.1")).thenReturn(21L);

        boolean allowed = rateLimiter.allow("table-token", "127.0.0.1");

        assertThat(allowed).isFalse();
    }

    @Test
    void redisFailureFailsOpen() {
        when(valueOperations.increment("qeat:rate:order:table-token:127.0.0.1"))
                .thenThrow(new RuntimeException("redis down"));

        boolean allowed = rateLimiter.allow("table-token", "127.0.0.1");

        assertThat(allowed).isTrue();
    }
}
