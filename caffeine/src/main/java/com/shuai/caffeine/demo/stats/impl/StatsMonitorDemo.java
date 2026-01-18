package com.shuai.caffeine.demo.stats.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 监控方案演示
 *
 * 定时收集缓存指标并输出：
 * - hitRate: 命中率
 * - missCount: 未命中次数
 * - evictionCount: 淘汰次数
 * - estimatedSize: 缓存条目数
 */
public class StatsMonitorDemo {

    public void runDemo() {
        System.out.println("\n--- 监控方案 (Scheduled Monitoring) ---");

        Cache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(10000)
                .recordStats()
                .build();

        // 填充数据并模拟访问
        for (int i = 0; i < 500; i++) {
            cache.put("key:" + i, "value:" + i);
        }
        for (int i = 0; i < 1000; i++) {
            cache.getIfPresent("key:" + (i % 500));
        }

        // 定时监控
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        AtomicLong queryCount = new AtomicLong(0);

        // 立即执行一次，然后每5秒输出
        scheduler.scheduleAtFixedRate(() -> {
            var stats = cache.stats();
            long count = queryCount.incrementAndGet();
            System.out.printf("  [Monitor#%d] hitRate=%.2f%%, missCount=%d, eviction=%d, size=%d%n",
                    count,
                    stats.hitRate() * 100,
                    stats.missCount(),
                    stats.evictionCount(),
                    cache.estimatedSize());
        }, 0, 5, TimeUnit.SECONDS);

        // 15秒后停止
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        scheduler.shutdown();
        System.out.println("  Monitor stopped.");
    }
}
