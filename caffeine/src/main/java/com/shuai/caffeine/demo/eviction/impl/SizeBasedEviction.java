package com.shuai.caffeine.demo.eviction.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shuai.caffeine.util.DemoConstants;

/**
 * 基于大小的淘汰演示
 *
 * 核心配置：maximumSize(N)
 * - 缓存条目达到 N 时触发淘汰
 * - 使用 Window TinyLFU 算法，保留热点数据
 * - estimatedSize() 返回预估容量
 */
public class SizeBasedEviction {

    public void runDemo() {
        System.out.println("\n--- 基于大小的淘汰 (Size-based Eviction) ---");

        Cache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(DemoConstants.SMALL_SIZE)
            .build();

        // 放入 10 个条目（超过 maximumSize）
        for (int i = 1; i <= 10; i++) {
            cache.put("key:" + i, "value:" + i);
        }

        System.out.println("  After putting 10 items, estimatedSize: " + cache.estimatedSize());

        // 访问 key:1 多次，使其成为热点
        for (int i = 0; i < 5; i++) {
            cache.getIfPresent("key:1");
        }

        // 继续放入新条目，触发淘汰
        for (int i = 11; i <= 13; i++) {
            cache.put("key:" + i, "value:" + i);
        }

        // 检查热点数据是否保留
        System.out.println("  After eviction, key:1 present: " + (cache.getIfPresent("key:1") != null));
        System.out.println("  key:11 present: " + (cache.getIfPresent("key:11") != null));
    }
}
