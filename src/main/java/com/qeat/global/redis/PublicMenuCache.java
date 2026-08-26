package com.qeat.global.redis;

import com.qeat.dto.menu.MenuResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;

@Component
public class PublicMenuCache {

    private static final Logger log = LoggerFactory.getLogger(PublicMenuCache.class);
    private static final TypeReference<List<MenuResponse>> MENU_LIST_TYPE = new TypeReference<>() {
    };

    private final StringRedisTemplate redis;
    private final JsonMapper jsonMapper;
    private final Duration ttl;

    public PublicMenuCache(
            StringRedisTemplate redis,
            @Value("${qeat.cache.public-menu-ttl:5m}") Duration ttl
    ) {
        this.redis = redis;
        this.jsonMapper = JsonMapper.builder().build();
        this.ttl = ttl;
    }

    public List<MenuResponse> get(Long boothId) {
        try {
            String json = redis.opsForValue().get(key(boothId));
            if (json == null || json.isBlank()) {
                return null;
            }
            return jsonMapper.readValue(json, MENU_LIST_TYPE);
        } catch (Exception e) {
            log.warn("Failed to read public menu cache for booth {}", boothId, e);
            return null;
        }
    }

    public void put(Long boothId, List<MenuResponse> menus) {
        try {
            redis.opsForValue().set(key(boothId), jsonMapper.writeValueAsString(menus), ttl);
        } catch (Exception e) {
            log.warn("Failed to write public menu cache for booth {}", boothId, e);
        }
    }

    public void evict(Long boothId) {
        try {
            redis.delete(key(boothId));
        } catch (Exception e) {
            log.warn("Failed to evict public menu cache for booth {}", boothId, e);
        }
    }

    private String key(Long boothId) {
        return "qeat:public:booth:" + boothId + ":menus";
    }
}
