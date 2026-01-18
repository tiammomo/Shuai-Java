package com.shuai.caffeine.demo.loading;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.shuai.caffeine.model.UserData;
import com.shuai.caffeine.util.DataLoader;
import com.shuai.caffeine.util.SleepUtils;

import java.time.Duration;

/**
 * LoadingCache 自动加载演示
 *
 * 核心特性:
 * - 缓存不存在时自动调用 loader 加载
 * - get 方法是阻塞的原子操作
 * - refreshAfterWrite: 可刷新但返回旧值
 * - expireAfterWrite: 超时后过期需重新加载
 */
public class LoadingCacheDemo {

    public void runAllDemos() {
        System.out.println("\n=== LoadingCache 自动加载 ===");

        loadingCacheIntro();
        cacheLoader();
        refreshPolicy();
        refreshVsExpire();
    }

    private void loadingCacheIntro() {
        System.out.println("\n--- LoadingCache 介绍 ---");
        System.out.println("  缓存不存在时自动调用 loader 加载");
        System.out.println("  避免缓存击穿");

        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .build(key -> "Loaded-" + key);

        System.out.println("  get('test'): " + cache.get("test"));
        System.out.println("  get('test') again: " + cache.get("test"));
    }

    private void cacheLoader() {
        System.out.println("\n--- CacheLoader 缓存加载器 ---");

        LoadingCache<String, UserData> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .build(key -> DataLoader.loadUserData(key));

        System.out.println("  get('user:1'): " + cache.get("user:1"));
        System.out.println("  get('user:1') again: " + cache.get("user:1"));
        System.out.println("  get('user:999'): " + cache.get("user:999"));
    }

    private void refreshPolicy() {
        System.out.println("\n--- 刷新策略 (refreshAfterWrite) ---");

        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .refreshAfterWrite(Duration.ofSeconds(1))
                .build(key -> {
                    System.out.println("    [Loader] Loading: " + key);
                    return DataLoader.loadStringData(key);
                });

        System.out.println("  首次访问:");
        String val1 = cache.get("key:1");
        System.out.println("    result: " + val1);

        System.out.println("  1秒内再次访问 (缓存):");
        String val2 = cache.get("key:1");
        System.out.println("    result: " + val2);

        System.out.println("  等待1秒...");
        SleepUtils.sleep(1100);

        System.out.println("  1秒后访问 (触发刷新):");
        String val3 = cache.get("key:1");
        System.out.println("    result: " + val3);
    }

    private void refreshVsExpire() {
        System.out.println("\n--- 刷新 vs 过期 ---");
        System.out.println("  expireAfterWrite: 超时后返回 null，需重新加载");
        System.out.println("  refreshAfterWrite: 超时可刷新，返回旧值直到刷新完成");
        System.out.println("  组合使用: refresh(5min) + expire(10min)");

        // 演示 expireAfterWrite
        Cache<String, String> expireCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(1))
                .build();
        expireCache.put("expireKey", "value");
        System.out.println("  expireAfterWrite demo:");
        System.out.println("    Initial: " + expireCache.getIfPresent("expireKey"));
        SleepUtils.sleep(1100);
        System.out.println("    After 1.1s: " + expireCache.getIfPresent("expireKey"));
    }
}
