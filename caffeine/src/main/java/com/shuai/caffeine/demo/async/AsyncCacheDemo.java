package com.shuai.caffeine.demo.async;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shuai.caffeine.util.SleepUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * AsyncCache 异步缓存演示
 *
 * 核心特性:
 * - 返回 CompletableFuture，非阻塞操作
 * - 默认使用 ForkJoinPool.commonPool()
 * - 适合高并发场景
 */
public class AsyncCacheDemo {

    public void runAllDemos() throws Exception {
        System.out.println("\n=== AsyncCache 异步缓存 ===");

        asyncCacheIntro();
        asyncCacheOperations();
        asyncLoadingCache();
        asyncVsSync();
        executorConfiguration();
    }

    private void asyncCacheIntro() {
        System.out.println("\n--- AsyncCache 介绍 ---");
        System.out.println("  返回 CompletableFuture，非阻塞");
        System.out.println("  默认使用 ForkJoinPool.commonPool()");

        AsyncCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .buildAsync();

        CompletableFuture<String> future = cache.get("key", k -> "Loaded-" + k);
        System.out.println("  Demo: future.isDone() = " + future.isDone());
    }

    private void asyncCacheOperations() throws Exception {
        System.out.println("\n--- 异步缓存操作 ---");

        AsyncCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .buildAsync();

        // get: 异步获取
        CompletableFuture<String> future1 = cache.get("key1", key -> {
            SleepUtils.sleep(50);
            return "Value-" + key;
        });
        System.out.println("  get(): future.isDone() = " + future1.isDone());
        System.out.println("  get().get() = " + future1.get());

        // getIfPresent: 异步查询
        CompletableFuture<String> future2 = cache.getIfPresent("key1");
        System.out.println("  getIfPresent(): " + (future2 != null ? future2.get() : "null"));

        // put: 异步写入
        cache.put("key2", CompletableFuture.completedFuture("Value2"));
        System.out.println("  put(): " + cache.getIfPresent("key2").get());

        // invalidate: 同步删除
        cache.synchronous().invalidate("key2");
        System.out.println("  invalidate(): " + cache.getIfPresent("key2"));
    }

    private void asyncLoadingCache() throws Exception {
        System.out.println("\n--- AsyncLoadingCache ---");

        var cache = Caffeine.newBuilder()
                .maximumSize(100)
                .buildAsync((key, executor) ->
                    CompletableFuture.supplyAsync(() -> {
                        SleepUtils.sleep(50);
                        return "AsyncValue-" + key;
                    }, executor)
                );

        // 并发访问
        System.out.println("  并发访问 3 个 key:");
        var futures = new CompletableFuture[3];
        for (int i = 1; i <= 3; i++) {
            futures[i - 1] = cache.get("async:" + i);
        }
        CompletableFuture.allOf(futures).get();

        for (int i = 0; i < 3; i++) {
            System.out.println("    async:" + (i + 1) + " -> " + futures[i].get());
        }

        // 批量加载
        var keys = Arrays.asList("a", "b", "c");
        var allFuture = cache.getAll(keys);
        System.out.println("  getAll([a,b,c]): " + allFuture.get());
    }

    private void asyncVsSync() throws Exception {
        System.out.println("\n--- 异步 vs 同步 ---");
        System.out.println("  同步: 阻塞等待结果");
        System.out.println("  异步: CompletableFuture，非阻塞");

        var asyncCache = Caffeine.newBuilder()
                .buildAsync((k, ex) -> CompletableFuture.completedFuture("Value-" + k));

        // 链式调用
        CompletableFuture<String> combined = asyncCache.get("a")
                .thenApply(v -> v + "-suffix")
                .thenApply(String::toUpperCase);
        System.out.println("  thenApply chain: " + combined.get());
    }

    private void executorConfiguration() throws Exception {
        System.out.println("\n--- Executor 配置 ---");
        System.out.println("  自定义线程池控制并发");

        ExecutorService executor = Executors.newFixedThreadPool(2);

        AsyncCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .buildAsync();

        System.out.println("  5 个任务，2 个线程:");
        var futures = new CompletableFuture[5];
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            futures[i - 1] = cache.get("task:" + taskId, (key, exec) ->
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("    [Thread-" + Thread.currentThread().getId() + "] " + key);
                    SleepUtils.sleep(50);
                    return "Done";
                }, exec)
            );
        }
        CompletableFuture.allOf(futures).get();

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);
        System.out.println("  executor.shutdown() completed.");
    }
}
