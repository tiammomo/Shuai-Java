package com.shuai.caffeine.demo.stats;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.shuai.caffeine.demo.stats.impl.StatsEnableDemo;
import com.shuai.caffeine.demo.stats.impl.StatsMetricsDemo;
import com.shuai.caffeine.demo.stats.impl.StatsMonitorDemo;

/**
 * Caffeine 缓存统计演示
 */
public class StatsDemo {

    public void runAllDemos() {
        System.out.println("\n=== Caffeine 缓存统计 ===");

        new StatsEnableDemo().runDemo();
        new StatsMetricsDemo().runDemo();
        statsAnalysis();
        new StatsMonitorDemo().runDemo();
        cacheHealthCheck();
    }

    /** 命中率分析与演示 */
    private void statsAnalysis() {
        System.out.println("\n--- 命中率分析 ---");

        // 高命中率场景
        Cache<String, String> goodCache = Caffeine.newBuilder()
                .maximumSize(100)
                .recordStats()
                .build();
        for (int i = 0; i < 100; i++) {
            goodCache.put("key:" + i, "value:" + i);
        }
        for (int i = 0; i < 1000; i++) {
            goodCache.getIfPresent("key:" + (i % 100));
        }
        CacheStats goodStats = goodCache.stats();
        System.out.println("  高命中率场景: " + String.format("%.1f%%", goodStats.hitRate() * 100));

        // 低命中率场景
        Cache<String, String> badCache = Caffeine.newBuilder()
                .maximumSize(10)
                .recordStats()
                .build();
        for (int i = 0; i < 100; i++) {
            badCache.put("key:" + i, "value:" + i);
        }
        for (int i = 0; i < 100; i++) {
            badCache.getIfPresent("key:" + i);
        }
        CacheStats badStats = badCache.stats();
        System.out.println("  低命中率场景: " + String.format("%.1f%%", badStats.hitRate() * 100));

        System.out.println("  命中率判断: >95%良好，<80%需检查策略");
    }

    /** 缓存健康检查演示 */
    private void cacheHealthCheck() {
        System.out.println("\n--- 缓存健康检查 ---");

        Cache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(10)
                .recordStats()
                .build();

        // 模拟高淘汰场景
        for (int i = 0; i < 100; i++) {
            cache.put("key:" + i, "value:" + i);
        }

        // 访问产生淘汰
        for (int i = 0; i < 200; i++) {
            cache.getIfPresent("key:" + (i % 100));
        }

        CacheStats stats = cache.stats();
        System.out.println("  hitRate: " + String.format("%.1f%%", stats.hitRate() * 100));
        System.out.println("  evictionCount: " + stats.evictionCount());
        System.out.println("  avgLoadTime: " + stats.averageLoadPenalty() + " ns");

        // 健康检查
        if (stats.hitRate() < 0.8) {
            System.out.println("  [警告] 命中率过低，考虑增加缓存容量");
        }
        if (stats.evictionCount() > 0) {
            System.out.println("  [提示] 存在淘汰，检查容量是否充足");
        }
        if (stats.averageLoadPenalty() > 1_000_000) {
            System.out.println("  [警告] 加载时间过长，需优化 loader");
        }
    }
}
