package com.shuai.caffeine;

import com.shuai.caffeine.demo.async.AsyncCacheDemo;
import com.shuai.caffeine.demo.basic.BasicUsageDemo;
import com.shuai.caffeine.demo.eviction.EvictionDemo;
import com.shuai.caffeine.demo.loading.LoadingCacheDemo;
import com.shuai.caffeine.demo.overview.CaffeineOverviewDemo;
import com.shuai.caffeine.demo.stats.StatsDemo;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine 高性能本地缓存库模块入口类
 *
 * 模块概述
 * ----------
 * 本模块系统性地演示了 Caffeine 高性能本地缓存库的核心知识和实践，
 * 涵盖核心概念、基础使用、LoadingCache、淘汰策略、统计信息、异步缓存等。
 *
 * 核心内容
 * ----------
 *   - CaffeineOverviewDemo: 核心概念、特性、与 Guava Cache 对比
 *   - BasicUsageDemo: Cache 接口、缓存操作 (get/put/invalidate)
 *   - LoadingCacheDemo: 自动加载缓存、CacheLoader、刷新策略
 *   - EvictionDemo: 淘汰策略 (容量、时间、权重、引用)
 *   - StatsDemo: 缓存统计、命中率分析
 *   - AsyncCacheDemo: 异步缓存、AsyncLoadingCache
 *
 * @author Shuai
 * @version 1.0
 * @see com.github.benmanes.caffeine.cache.Caffeine
 */
public class CaffeineDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(50));
        System.out.println("       Caffeine 高性能本地缓存");
        System.out.println("=".repeat(50));

        // 核心概念
        new CaffeineOverviewDemo().runAllDemos();

        // 基础使用
        new BasicUsageDemo().runAllDemos();

        // 自动加载缓存
        new LoadingCacheDemo().runAllDemos();

        // 淘汰策略
        new EvictionDemo().runAllDemos();

        // 统计信息
        new StatsDemo().runAllDemos();

        // 异步缓存
        new AsyncCacheDemo().runAllDemos();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Caffeine 模块演示完成");
        System.out.println("=".repeat(50));

        // 关闭 ForkJoinPool 公共线程池（消除线程警告）
        ForkJoinPool.commonPool().shutdown();
        try {
            if (!ForkJoinPool.commonPool().awaitTermination(1, TimeUnit.SECONDS)) {
                ForkJoinPool.commonPool().shutdownNow();
            }
        } catch (InterruptedException e) {
            ForkJoinPool.commonPool().shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 相关资源（见 README.md）
        // GitHub: https://github.com/ben-manes/caffeine
        // Wiki: https://github.com/ben-manes/caffeine/wiki
    }
}
