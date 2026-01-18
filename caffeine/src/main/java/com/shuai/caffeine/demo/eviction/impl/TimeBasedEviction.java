package com.shuai.caffeine.demo.eviction.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shuai.caffeine.util.SleepUtils;

import java.time.Duration;

/**
 * 基于时间的淘汰演示
 *
 * 核心配置：
 * - expireAfterWrite: 写入后经过指定时间过期
 * - expireAfterAccess: 最后访问后经过指定时间过期
 */
public class TimeBasedEviction {

    public void runDemo() {
        System.out.println("\n--- 基于时间的淘汰 (Time-based Eviction) ---");

        // expireAfterWrite: 写入后 1 秒过期
        Cache<String, String> cache1 = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(1))
            .build();

        cache1.put("key1", "value1");
        System.out.println("  expireAfterWrite - Initial: " + cache1.getIfPresent("key1"));

        SleepUtils.sleep(1100);
        System.out.println("  expireAfterWrite - After 1.1s: " + cache1.getIfPresent("key1"));

        // expireAfterAccess: 最后访问后 1 秒过期
        Cache<String, String> cache2 = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofSeconds(1))
            .build();

        cache2.put("key2", "value2");
        System.out.println("  expireAfterAccess - After put: " + cache2.getIfPresent("key2"));

        SleepUtils.sleep(1100);
        System.out.println("  expireAfterAccess - After 1.1s (no access): " + cache2.getIfPresent("key2"));

        // 访问刷新过期时间
        cache2.put("key3", "value3");
        cache2.getIfPresent("key3");
        SleepUtils.sleep(500);
        System.out.println("  expireAfterAccess - After 0.5s (accessed): " + cache2.getIfPresent("key3"));
        SleepUtils.sleep(600);
        System.out.println("  expireAfterAccess - After another 0.6s: " + cache2.getIfPresent("key3"));
    }
}
