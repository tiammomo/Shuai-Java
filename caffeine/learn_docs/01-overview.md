# 01 - Caffeine 概述

## 1.1 什么是 Caffeine

Caffeine 是一个高性能的本地缓存库，基于 Java 8 开发，性能比 Guava Cache 快 5-10 倍。它采用 **Window TinyLFU** 算法，能够有效应对热点数据变化。

### 核心特性

| 特性 | 说明 |
|------|------|
| 高性能 | Window TinyLFU 算法，接近最优命中率 |
| 丰富淘汰策略 | 容量、时间、权重、引用多种策略 |
| 原生异步支持 | AsyncCache、AsyncLoadingCache |
| 实时统计 | recordStats() 实时收集统计信息 |
| Spring 集成 | Spring Boot Cache 自动配置 |

详见 [CaffeineOverviewDemo.java:48-62](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L48-L62)

### Maven 依赖

```xml
<dependency>
    <groupId>com.github.benmanes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>3.1.8</version>
</dependency>
```

详见 [CaffeineOverviewDemo.java:51-59](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L51-L59)

---

## 1.2 Caffeine vs Guava Cache

| 特性 | Caffeine | Guava Cache |
|------|----------|-------------|
| 性能 | 更高 | 高 |
| 异步 | 原生支持 | 需额外配置 |
| 淘汰策略 | 更丰富 | 丰富 |
| 统计 | 实时 | 采样 |
| 算法 | Window TinyLFU | LRU |
| API | 更简洁 | 简洁 |

详见 [CaffeineOverviewDemo.java:65-86](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L65-L86)

### Window TinyLFU 优势

```
Window TinyLFU 优势:
  - 适应热点数据变化
  - 更好的内存利用率
  - 减少缓存污染
```

详见 [CaffeineOverviewDemo.java:69-73](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L69-L73)

---

## 1.3 使用场景

| 场景 | 说明 | 示例代码位置 |
|------|------|--------------|
| 本地热点数据缓存 | 存储频繁访问的数据 | [CaffeineOverviewDemo.java:123-130](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L123-L130) |
| 分布式缓存本地副本 | 减少远程调用 | 同上 |
| 页面片段缓存 | 缓存页面组件 | 同上 |
| API 响应缓存 | 缓存 API 返回结果 | 同上 |
| 计数器/限流窗口 | 滑动窗口计数 | 同上 |

---

## 1.4 实践要点

### 何时使用 Caffeine

- 需要高性能本地缓存
- 热点数据变化频繁
- 需要实时统计信息
- 需要异步缓存支持
- Spring Boot 应用需要缓存集成

### 注意事项

- 不会自动清理 OS 缓存页，内存敏感场景需评估
- 最大容量受 JVM 堆内存限制
- 不支持分布式，需要分布式缓存请用 Redis

---

## 1.5 相关代码

| 功能 | 文件位置 |
|------|----------|
| 核心概念演示 | [CaffeineOverviewDemo.java](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java) |
| 特性对比 | [CaffeineOverviewDemo.java:48-86](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L48-L86) |
| 使用场景 | [CaffeineOverviewDemo.java:123-138](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L123-L138) |

---

## 1.6 课后练习

1. 运行演示查看 Caffeine vs Guava 对比输出
2. 理解 Window TinyLFU 算法原理
3. 分析项目中适合使用 Caffeine 的场景

**下一章**: [02 - 四种缓存类型](02-cache-types.md)
