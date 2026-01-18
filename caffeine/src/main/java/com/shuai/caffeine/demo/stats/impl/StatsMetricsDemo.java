package com.shuai.caffeine.demo.stats.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

/**
 * 统计指标演示
 *
 * 常用统计指标：
 * - hitCount/missCount: 命中/未命中次数
 * - hitRate/missRate: 命中率/未命中率
 * - evictionCount/evictionWeight: 淘汰次数/权重
 * - averageLoadPenalty: 平均加载时间(纳秒)
 */
public class StatsMetricsDemo {

    public void runDemo() {
        System.out.println("\n--- 统计指标 (CacheStats) ---");

        Cache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .recordStats()
                .build();

        // 填充缓存
        for (int i = 0; i < 100; i++) {
            cache.put("user:" + i, "User-" + i);
        }
        // 访问：70% 命中，30% 未命中
        for (int i = 0; i < 200; i++) {
            cache.getIfPresent("user:" + (i % 100));  // 命中前100个
        }
        for (int i = 100; i < 150; i++) {
            cache.getIfPresent("user:" + i);  // 未命中
        }

        CacheStats stats = cache.stats();

        System.out.println("  hitCount: " + stats.hitCount());
        System.out.println("  missCount: " + stats.missCount());
        System.out.println("  evictionCount: " + stats.evictionCount());
        System.out.println("  hitRate: " + String.format("%.2f%%", stats.hitRate() * 100));
        System.out.println("  missRate: " + String.format("%.2f%%", stats.missRate() * 100));
        System.out.println("  avgLoadTime: " + stats.averageLoadPenalty() + " ns");
    }
}
