package com.shuai.caffeine.demo.eviction.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shuai.caffeine.util.DemoConstants;

/**
 * 基于权重的淘汰演示
 *
 * 核心配置：
 * - maximumWeight(N): 设置最大权重
 * - weigher(key, value): 自定义权重计算
 * - 适合条目大小不均匀的场景
 */
public class WeightBasedEviction {

    public void runDemo() {
        System.out.println("\n--- 基于权重的淘汰 (Weight-based Eviction) ---");

        // 权重单位：bytes，最大 1KB
        Cache<String, byte[]> cache = Caffeine.newBuilder()
            .maximumWeight(DemoConstants.MAX_WEIGHT_BYTES)
            .weigher((key, value) -> ((byte[]) value).length)
            .build();

        // 添加不同大小的数据
        cache.put("small", new byte[100]);   // 100 bytes
        cache.put("medium", new byte[500]);  // 500 bytes
        cache.put("large", new byte[600]);   // 600 bytes

        System.out.println("  Added 3 items (100 + 500 + 600 = 1200 bytes)");
        System.out.println("  estimatedSize: " + cache.estimatedSize());

        // 继续添加触发淘汰
        cache.put("extra", new byte[400]);  // 400 bytes
        System.out.println("  Added 400 bytes item, estimatedSize: " + cache.estimatedSize());

        // 检查哪些被保留
        System.out.println("  small present: " + (cache.getIfPresent("small") != null));
        System.out.println("  extra present: " + (cache.getIfPresent("extra") != null));
    }
}
