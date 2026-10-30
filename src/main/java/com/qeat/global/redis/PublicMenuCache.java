package com.qeat.global.redis;

import com.qeat.dto.menu.MenuResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
    private final boolean enabled;

    public PublicMenuCache(
            StringRedisTemplate redis,
            @Value("${qeat.cache.public-menu-ttl:5m}") Duration ttl,
            @Value("${qeat.cache.public-menu.enabled:true}") boolean enabled
    ) {
        this.redis = redis;
        this.jsonMapper = JsonMapper.builder().build();
        this.ttl = ttl;
        this.enabled = enabled;
    }

    public List<MenuResponse> get(Long boothId) {
        if (!enabled) {
            return null;
        }
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
        if (!enabled) {
            return;
        }
        try {
            redis.opsForValue().set(key(boothId), jsonMapper.writeValueAsString(menus), ttl);
        } catch (Exception e) {
            log.warn("Failed to write public menu cache for booth {}", boothId, e);
        }
    }

    /**
     * 메뉴 변경 트랜잭션이 커밋된 뒤에 캐시를 지운다.
     * 커밋 전에 지우면, 그 사이 들어온 조회가 아직 커밋되지 않은 옛 메뉴를 다시 캐시에 넣어
     * 품절 상태가 최대 TTL(5분) 동안 틀리게 보일 수 있다.
     */
    public void evictAfterCommit(Long boothId) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    evict(boothId);
                }
            });
            return;
        }
        evict(boothId);
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
