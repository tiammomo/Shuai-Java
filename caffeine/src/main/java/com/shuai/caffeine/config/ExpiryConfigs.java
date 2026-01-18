package com.shuai.caffeine.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;

import java.time.Duration;

/**
 * 过期策略配置
 */
public final class ExpiryConfigs {

    private ExpiryConfigs() {}

    // === 基础过期 ===

    public static Caffeine<Object, Object> expireAfterWrite(Duration d) {
        return Caffeine.newBuilder().expireAfterWrite(d);
    }

    public static Caffeine<Object, Object> expireAfterAccess(Duration d) {
        return Caffeine.newBuilder().expireAfterAccess(d);
    }

    public static <K, V> Caffeine<K, V> withCustomExpiry(Expiry<K, V> expiry) {
        return Caffeine.newBuilder().expireAfter(expiry);
    }

    // === 预定义过期 ===

    public static Caffeine<Object, Object> expireAfterWrite1s() {
        return expireAfterWrite(Duration.ofSeconds(1));
    }

    public static Caffeine<Object, Object> expireAfterWrite5min() {
        return expireAfterWrite(Duration.ofMinutes(5));
    }

    public static Caffeine<Object, Object> expireAfterWrite1h() {
        return expireAfterWrite(Duration.ofHours(1));
    }

    public static Caffeine<Object, Object> expireAfterAccess5min() {
        return expireAfterAccess(Duration.ofMinutes(5));
    }

    // === 组合策略 ===

    /** 刷新 + 强制过期 */
    public static Caffeine<Object, Object> refreshAndExpire(Duration refresh, Duration expire) {
        return Caffeine.newBuilder()
                .refreshAfterWrite(refresh)
                .expireAfterWrite(expire);
    }
}
