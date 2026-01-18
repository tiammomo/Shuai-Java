package com.shuai.caffeine.demo.eviction.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 淘汰监听器演示
 *
 * RemovalListener: 监听缓存淘汰事件
 * - EXPLICIT: 手动删除
 * - SIZE: 超过大小限制
 * - TIME: 时间过期
 * - COLLECTED: 被 GC 回收
 * - EXPIRED: 过期
 */
public class RemovalListenerDemo {

    public void runDemo() {
        System.out.println("\n--- 淘汰监听器 (RemovalListener) ---");

        Cache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(3)
            .removalListener((key, value, cause) -> {
                System.out.println("    [Removed] key=" + key + ", cause=" + cause);
            })
            .build();

        // 添加 3 个条目
        cache.put("a", "1");
        cache.put("b", "2");
        cache.put("c", "3");
        System.out.println("  After adding 3 items, size: " + cache.estimatedSize());

        // 添加第 4 个，触发淘汰
        cache.put("d", "4");
        System.out.println("  After adding 4th item, size: " + cache.estimatedSize());

        // 手动删除
        cache.invalidate("d");
        System.out.println("  After invalidating 'd', size: " + cache.estimatedSize());
    }
}
