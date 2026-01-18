# 06 - 统计与监控

## 6.1 启用统计 (recordStats)

Caffeine 支持实时缓存统计，需要显式启用。

```java
Cache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(100)
    .recordStats()  // 启用统计
    .build();
```

详见 [StatsEnableDemo.java](../src/main/java/com/shuai/caffeine/demo/stats/impl/StatsEnableDemo.java)

### 性能影响

recordStats 会对性能有轻微影响：
- 增加少量 CPU 开销
- 需要额外内存存储统计信息
- 适合需要监控命中率的场景

---

## 6.2 常用统计指标

通过 cache.stats() 获取统计信息。

```java
CacheStats stats = cache.stats();

stats.hitCount()        // 命中次数
stats.missCount()       // 未命中次数
stats.hitRate()         // 命中率 (0-1)
stats.missRate()        // 未命中率
stats.evictionCount()   // 淘汰次数
stats.evictionWeight()  // 淘汰权重
stats.loadSuccessCount()// 加载成功次数
stats.loadFailureCount()// 加载失败次数
stats.averageLoadPenalty()// 平均加载耗时（纳秒）
```

详见 [StatsMetricsDemo.java](../src/main/java/com/shuai/caffeine/demo/stats/impl/StatsMetricsDemo.java)

### 命中率分析

```java
double hitRate = stats.hitRate();
double missRate = stats.missRate();

// 格式化输出
System.out.printf("命中率: %.2f%%%n", hitRate * 100);
System.out.printf("未命中率: %.2f%%%n", missRate * 100);
```

详见 [StatsDemo.java](../src/main/java/com/shuai/caffeine/demo/stats/StatsDemo.java)

---

## 6.3 缓存健康检查

Caffeine 缓存健康检查最佳实践。

```java
CacheStats stats = cache.stats();

if (stats.hitRate() < 0.8) {
    System.out.println("[警告] 命中率过低，考虑增加缓存容量");
}
if (stats.evictionCount() > 0) {
    System.out.println("[提示] 存在淘汰，检查容量是否充足");
}
if (stats.averageLoadPenalty() > 1_000_000) {
    System.out.println("[警告] 加载时间过长，需优化 loader");
}
```

详见 [StatsDemo.java](../src/main/java/com/shuai/caffeine/demo/stats/StatsDemo.java)

### 适用场景

- 集成到公司监控系统
- 设置命中率告警
- 定期分析优化配置

---

## 6.4 监控方案

### 定时监控

Caffeine 可以通过定时任务收集统计信息。

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

scheduler.scheduleAtFixedRate(() -> {
    CacheStats stats = cache.stats();
    System.out.printf("hitRate=%.2f%%, missCount=%d, eviction=%d%n",
            stats.hitRate() * 100,
            stats.missCount(),
            stats.evictionCount());
}, 0, 5, TimeUnit.SECONDS);
```

详见 [StatsMonitorDemo.java](../src/main/java/com/shuai/caffeine/demo/stats/impl/StatsMonitorDemo.java)

### 监控指标

| 指标 | 说明 |
|------|------|
| cache.size | 缓存条目数 |
| cache.hitRate | 命中率 |
| cache.evictions | 淘汰数 |
| cache.loadTime | 加载时间 |

---

## 6.5 代码位置汇总

| 功能 | 文件位置 |
|------|----------|
| 启用统计 | [StatsEnableDemo.java](../src/main/java/com/shuai/caffeine/demo/stats/impl/StatsEnableDemo.java) |
| 统计指标 | [StatsMetricsDemo.java](../src/main/java/com/shuai/caffeine/demo/stats/impl/StatsMetricsDemo.java) |
| 命中率分析 | [StatsDemo.java](../src/main/java/com/shuai/caffeine/demo/stats/StatsDemo.java) |
| 缓存健康检查 | [StatsDemo.java](../src/main/java/com/shuai/caffeine/demo/stats/StatsDemo.java) |
| 监控方案 | [StatsMonitorDemo.java](../src/main/java/com/shuai/caffeine/demo/stats/impl/StatsMonitorDemo.java) |

---

## 6.6 实践要点

### 监控建议

1. **生产环境必须启用** - 便于问题排查
2. **关注命中率** - 过低说明缓存配置不合理
3. **设置告警** - 命中率异常时及时告警
4. **定期分析** - 根据统计数据优化配置

### 命中率参考

| 命中率 | 评估 |
|--------|------|
| > 95% | 优秀 |
| 80-95% | 良好 |
| < 80% | 需要优化 |

---

## 6.7 课后练习

1. 运行 StatsDemo 查看统计输出
2. 为项目缓存添加监控
3. 根据统计数据优化缓存配置

**下一章**: [07 - 异步缓存](07-async-cache.md)
