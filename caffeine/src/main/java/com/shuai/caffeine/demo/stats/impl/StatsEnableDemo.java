package com.shuai.caffeine.demo.stats.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 启用统计演示
 *
 * 核心配置：recordStats()
 * - 启用后可通过 cache.stats() 获取统计信息
 * - 有轻微性能开销，生产环境建议启用
 */
public class StatsEnableDemo {

    public void runDemo() {
        System.out.println("\n--- 启用统计 (recordStats) ---");

        // 启用统计
        Cache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .recordStats()
                .build();

        // 执行缓存操作
        for (int i = 0; i < 100; i++) {
            cache.put("key:" + i, "value:" + i);
            cache.getIfPresent("key:" + i);      // 命中
            cache.getIfPresent("missing:" + i);  // 未命中
        }

        System.out.println("  " + cache.stats());
    }
}
