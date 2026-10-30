package com.qeat.global.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class PublicMenuCacheTest {

    private static final String KEY = "qeat:public:booth:1:menus";

    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    private final PublicMenuCache cache = new PublicMenuCache(redis, Duration.ofMinutes(5), true);

    @AfterEach
    void clearSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void evictAfterCommit_waitsUntilTransactionCommits() {
        TransactionSynchronizationManager.initSynchronization();

        cache.evictAfterCommit(1L);
        verify(redis, never()).delete(KEY);

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(redis).delete(KEY);
    }

    @Test
    void evictAfterCommit_skipsEvictionWhenTransactionRollsBack() {
        TransactionSynchronizationManager.initSynchronization();

        cache.evictAfterCommit(1L);
        TransactionSynchronizationManager.getSynchronizations()
                .forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));

        verify(redis, never()).delete(KEY);
    }

    @Test
    void evictAfterCommit_evictsImmediatelyWithoutTransaction() {
        cache.evictAfterCommit(1L);

        verify(redis).delete(KEY);
    }

    @Test
    void disabledCache_neverTouchesRedis() {
        PublicMenuCache disabled = new PublicMenuCache(redis, Duration.ofMinutes(5), false);

        disabled.get(1L);
        disabled.put(1L, java.util.List.of());

        verifyNoInteractions(redis);
    }
}
