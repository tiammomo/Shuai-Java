package com.shuai.caffeine.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExpiryConfigs 单元测试
 */
class ExpiryConfigsTest {

    @Test
    void testExpireAfterWrite() {
        Caffeine<Object, Object> builder = ExpiryConfigs.expireAfterWrite(Duration.ofSeconds(5));
        assertNotNull(builder);
        Cache<String, String> cache = builder.build();
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
    }

    @Test
    void testExpireAfterAccess() {
        Caffeine<Object, Object> builder = ExpiryConfigs.expireAfterAccess(Duration.ofSeconds(5));
        assertNotNull(builder);
        Cache<String, String> cache = builder.build();
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
    }

    @Test
    void testWithCustomExpiry() {
        Expiry<String, String> customExpiry = new Expiry<>() {
            @Override
            public long expireAfterCreate(String key, String value, long currentTime) {
                return Duration.ofSeconds(10).toNanos();
            }
            @Override
            public long expireAfterUpdate(String key, String value, long currentTime, long currentDuration) {
                return currentDuration;
            }
            @Override
            public long expireAfterRead(String key, String value, long currentTime, long currentDuration) {
                return currentDuration;
            }
        };
        Caffeine<String, String> builder = ExpiryConfigs.withCustomExpiry(customExpiry);
        assertNotNull(builder);
        Cache<String, String> cache = builder.build();
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
    }

    @Test
    void testExpireAfterWrite1s() {
        Cache<String, String> cache = ExpiryConfigs.expireAfterWrite1s().build();
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
    }

    @Test
    void testExpireAfterWrite5min() {
        Cache<String, String> cache = ExpiryConfigs.expireAfterWrite5min().build();
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
    }

    @Test
    void testExpireAfterWrite1h() {
        Cache<String, String> cache = ExpiryConfigs.expireAfterWrite1h().build();
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
    }

    @Test
    void testExpireAfterAccess5min() {
        Cache<String, String> cache = ExpiryConfigs.expireAfterAccess5min().build();
        cache.put("key", "value");
        assertEquals("value", cache.getIfPresent("key"));
    }

    @Test
    void testRefreshAndExpire() {
        Caffeine<Object, Object> builder = ExpiryConfigs.refreshAndExpire(
                Duration.ofMinutes(1),
                Duration.ofMinutes(5)
        );
        assertNotNull(builder);
        LoadingCache<String, String> cache = ((Caffeine<String, String>) (Caffeine<?, ?>) builder)
                .build(key -> "value");
        assertEquals("value", cache.get("key"));
    }

    @Test
    void testDifferentConfigsReturnDifferentBuilders() {
        Caffeine<Object, Object> builder1 = ExpiryConfigs.expireAfterWrite(Duration.ofSeconds(1));
        Caffeine<Object, Object> builder2 = ExpiryConfigs.expireAfterWrite(Duration.ofSeconds(2));
        assertNotSame(builder1, builder2);
    }
}
