package com.shuai.guava;

import com.google.common.cache.*;
import com.google.common.collect.Lists;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Guava 缓存演示
 *
 * @author Shuai
 * @version 1.0
 */
public class BasicsGuavaCacheDemo {

    public void runAllDemos() throws InterruptedException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Guava 缓存");
        System.out.println("=".repeat(50));

        cacheBuilder();
        loadingCache();
        cacheStats();
        expirePolicy();
        removalListener();
    }

    /**
     * CacheBuilder 基本用法
     */
    private void cacheBuilder() {
        System.out.println("\n--- CacheBuilder ---");

        System.out.println("  CacheBuilder.newBuilder()");
        System.out.println("      .maximumSize(100)          // 最大容量");
        System.out.println("      .expireAfterWrite(1, TimeUnit.HOURS)   // 写入过期");
        System.out.println("      .expireAfterAccess(30, TimeUnit.MINUTES) // 访问过期");
        System.out.println("      .weakKeys()                // 弱引用键");
        System.out.println("      .softValues()              // 软引用值");
        System.out.println("      .recordStats()             // 记录统计");
        System.out.println("      .concurrencyLevel(4)      // 并发级别");

        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();

        cache.put("key1", "value1");
        System.out.println("  放入 key1: " + cache.getIfPresent("key1"));

        cache.invalidate("key1");
        System.out.println("  删除后: " + cache.getIfPresent("key1"));
    }

    /**
     * LoadingCache 自动加载
     */
    private void loadingCache() throws InterruptedException {
        System.out.println("\n--- LoadingCache ---");

        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .recordStats()
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) {
                        return "loaded_" + key;
                    }
                });

        // 手动放入
        cache.put("key1", "value1");
        try {
            System.out.println("  put() 放入: key1 -> " + cache.get("key1"));
        } catch (ExecutionException e) {
            // ignore
        }

        // 自动加载
        try {
            String value = cache.get("key2");
            System.out.println("  get() 自动加载: key2 -> " + value);
        } catch (ExecutionException e) {
            // ignore
        }

        // getIfPresent 不触发加载
        String present = cache.getIfPresent("key2");
        System.out.println("  getIfPresent(): " + present);

        // 批量加载
        try {
            cache.getAll(Lists.newArrayList("a", "b", "c"));
        } catch (ExecutionException e) {
            // ignore
        }
    }

    /**
     * 统计信息
     */
    private void cacheStats() throws InterruptedException {
        System.out.println("\n--- 统计信息 ---");

        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .recordStats()
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) {
                        return "value_" + key;
                    }
                });

        // 触发缓存操作
        try {
            cache.get("a");
            cache.get("a");
            cache.get("b");
        } catch (ExecutionException e) {
            // ignore
        }
        cache.getIfPresent("c");

        Thread.sleep(100);

        CacheStats stats = cache.stats();
        System.out.println("  命中率: " + String.format("%.2f%%", stats.hitRate() * 100));
        System.out.println("  请求数: " + stats.requestCount());
        System.out.println("  命中次数: " + stats.hitCount());
        System.out.println("  未命中次数: " + stats.missCount());
        System.out.println("  淘汰次数: " + stats.evictionCount());
    }

    /**
     * 过期策略
     */
    private void expirePolicy() throws InterruptedException {
        System.out.println("\n--- 过期策略 ---");

        System.out.println("  expireAfterWrite - 写入后多长时间过期");
        LoadingCache<String, String> writeCache = CacheBuilder.newBuilder()
                .expireAfterWrite(2, TimeUnit.SECONDS)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) {
                        return "expire_" + key;
                    }
                });

        writeCache.put("temp", "data");
        System.out.println("  初始值: " + writeCache.getIfPresent("temp"));

        Thread.sleep(3000);
        System.out.println("  3秒后: " + writeCache.getIfPresent("temp"));
    }

    /**
     * 移除监听器
     */
    private void removalListener() {
        System.out.println("\n--- 移除监听器 ---");

        AtomicInteger removalCount = new AtomicInteger();

        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(3)
                .removalListener(notification -> {
                    System.out.println("  移除: " + notification.getKey() + " = " + notification.getValue());
                    System.out.println("  原因: " + notification.getCause());
                    removalCount.incrementAndGet();
                })
                .build();

        cache.put("1", "a");
        cache.put("2", "b");
        cache.put("3", "c");
        cache.put("4", "d");  // 触发移除

        System.out.println("  移除次数: " + removalCount.get());
    }
}
