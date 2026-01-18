package com.shuai.caffeine.demo.basic;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caffeine 基础使用演示
 *
 * 核心操作:
 * - getIfPresent: 查询（不创建条目）
 * - get: 原子查询（自动加载）
 * - put: 写入
 * - invalidate: 删除
 */
public class BasicUsageDemo {

    public void runAllDemos() {
        System.out.println("\n=== Caffeine 基础使用 ===");

        cacheInterface();
        getOperations();
        putOperations();
        invalidateOperations();
        batchOperations();
    }

    private void cacheInterface() {
        System.out.println("\n--- Cache 接口 ---");
        Cache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        System.out.println("  put key1/key2 -> size: " + cache.estimatedSize());
    }

    private void getOperations() {
        System.out.println("\n--- 查询操作 ---");
        Cache<String, String> cache = Caffeine.newBuilder().build();
        cache.put("user:1", "Alice");
        cache.put("user:2", "Bob");

        // getIfPresent: 不存在返回 null
        System.out.println("  getIfPresent: " + cache.getIfPresent("user:1"));
        System.out.println("  getIfPresent(null): " + cache.getIfPresent("user:3"));

        // get: 原子操作，不存在时调用 loader
        String value = cache.get("user:3", key -> "Default-" + key);
        System.out.println("  get(user:3, loader): " + value);
    }

    private void putOperations() {
        System.out.println("\n--- 写入操作 ---");
        Cache<String, String> cache = Caffeine.newBuilder().build();

        cache.put("key1", "value1");
        System.out.println("  put(key1, value1): " + cache.getIfPresent("key1"));

        // 批量写入
        Map<String, String> batch = new ConcurrentHashMap<>();
        for (int i = 1; i <= 5; i++) {
            batch.put("batch:" + i, "BatchValue-" + i);
        }
        cache.putAll(batch);
        System.out.println("  putAll(5 items): size = " + cache.estimatedSize());
    }

    private void invalidateOperations() {
        System.out.println("\n--- 删除操作 ---");
        Cache<String, String> cache = Caffeine.newBuilder().build();
        cache.put("a", "1");
        cache.put("b", "2");
        cache.put("c", "3");
        System.out.println("  Initial size: " + cache.estimatedSize());

        cache.invalidate("a");
        System.out.println("  invalidate('a'): size = " + cache.estimatedSize());

        cache.invalidateAll(Arrays.asList("b", "c"));
        System.out.println("  invalidateAll([b, c]): size = " + cache.estimatedSize());

        cache.put("x", "1");
        cache.invalidateAll();
        System.out.println("  invalidateAll(): size = " + cache.estimatedSize());
    }

    private void batchOperations() {
        System.out.println("\n--- 批量操作 ---");
        Cache<String, String> cache = Caffeine.newBuilder().build();

        for (int i = 1; i <= 10; i++) {
            cache.put("user:" + i, "User-" + i);
        }

        var keys = Arrays.asList("user:1", "user:3", "user:5", "user:99");
        var result = cache.getAllPresent(keys);
        System.out.println("  getAllPresent([1,3,5,99]): " + result.size() + " hits");
        System.out.println("  Result: " + result);
    }
}
