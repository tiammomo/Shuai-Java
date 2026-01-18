package com.shuai.caffeine.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.time.Duration;

/**
 * 缓存配置模板
 */
public final class CacheConfigs {

    private CacheConfigs() {}

    // === 简单缓存 ===

    public static Cache<String, String> smallCache() {
        return Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }

    public static Cache<String, String> mediumCache() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }

    public static Cache<String, String> largeCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .recordStats()
                .build();
    }

    public static Cache<String, String> unboundedCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }

    // === LoadingCache ===

    public static LoadingCache<String, String> smallLoadingCache() {
        return Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(Duration.ofMinutes(5))
                .build(key -> "Loaded-" + key);
    }

    public static LoadingCache<String, String> mediumLoadingCacheWithRefresh() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofMinutes(5))
                .refreshAfterWrite(Duration.ofMinutes(1))
                .build(key -> "Loaded-" + key);
    }

    // === 权重缓存 ===

    @SuppressWarnings("unchecked")
    public static Cache<String, byte[]> weightedCache() {
        return Caffeine.newBuilder()
                .maximumWeight(1024 * 1024)
                .weigher((key, value) -> ((byte[]) value).length)
                .build();
    }
}
