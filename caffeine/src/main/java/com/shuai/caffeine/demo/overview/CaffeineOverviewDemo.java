package com.shuai.caffeine.demo.overview;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.time.Duration;

/**
 * Caffeine 核心概念演示
 *
 * 缓存类型:
 * - Cache: 手动管理缓存
 * - LoadingCache: 自动加载缓存
 * - AsyncCache: 异步缓存
 */
public class CaffeineOverviewDemo {

    public void runAllDemos() {
        System.out.println("\n=== Caffeine 核心概念 ===");

        caffeineFeatures();
        cacheTypes();
        useCases();
    }

    private void caffeineFeatures() {
        System.out.println("\n--- Caffeine 特性 ---");
        System.out.println("  高性能: 比 Guava Cache 快 5-10 倍");
        System.out.println("  淘汰策略: 大小/时间/权重/引用");
        System.out.println("  原生异步支持: AsyncCache");
        System.out.println("  实时统计: recordStats()");
        System.out.println("  算法: Window TinyLFU");

        // 实际演示
        Cache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .build();
        cache.put("key", "value");
        System.out.println("  Demo: put/get -> " + cache.getIfPresent("key"));
    }

    private void cacheTypes() {
        System.out.println("\n--- 缓存类型 ---");

        // Cache - 手动管理
        Cache<String, String> cache = Caffeine.newBuilder().build();
        cache.put("cache", "CacheValue");
        System.out.println("  Cache: " + cache.getIfPresent("cache"));

        // LoadingCache - 自动加载
        LoadingCache<String, String> loadingCache = Caffeine.newBuilder()
                .build(key -> "Loaded-" + key);
        System.out.println("  LoadingCache: " + loadingCache.get("loader"));

        // AsyncCache - 异步
        System.out.println("  AsyncCache: (见 AsyncCacheDemo)");
    }

    private void useCases() {
        System.out.println("\n--- 使用场景 ---");
        System.out.println("  1. 本地热点数据缓存");
        System.out.println("  2. 分布式缓存本地副本");
        System.out.println("  3. API 响应缓存");
        System.out.println("  4. 数据库查询缓存");
    }
}
