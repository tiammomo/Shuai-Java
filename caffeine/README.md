# Caffeine 模块

> 高性能本地缓存库模块

## 目录

- [简介](#简介)
- [文件结构](#文件结构)
- [快速开始](#快速开始)
- [核心内容](#核心内容)
  - [1. 核心概念](#1-核心概念)
  - [2. 基础使用](#2-基础使用)
  - [3. LoadingCache 自动加载](#3-loadingcache-自动加载)
  - [4. 淘汰策略](#4-淘汰策略)
  - [5. 统计信息](#5-统计信息)
  - [6. 异步缓存](#6-异步缓存)
- [运行示例](#运行示例)
- [执行测试](#执行测试)
- [skills](#skills)

## 简介

Caffeine 是一个高性能的本地缓存库，性能比 Guava Cache 快 5-10 倍。它采用 Window TinyLFU 算法，能够有效应对热点数据变化，提供丰富的淘汰策略和实时统计信息。

**核心特性**：
- 高性能：基于 Window TinyLFU 算法
- 丰富淘汰策略：容量、时间、权重、引用
- 原生异步支持：AsyncCache、AsyncLoadingCache
- 实时统计：recordStats()
- Spring Cache 集成

## 文件结构

```
src/main/java/com/shuai/caffeine/
├── CaffeineDemo.java                    # 模块入口，编排各演示类

# 工具类 - 公共帮助方法
└── util/
    ├── SleepUtils.java                  # 睡眠工具
    ├── DataLoader.java                  # 模拟数据加载
    └── DemoConstants.java               # 常量类

# 配置类 - 预定义配置模板
└── config/
    ├── CacheConfigs.java                # 预定义缓存配置模板
    └── ExpiryConfigs.java               # 过期策略配置模板

# 演示模块 - 按功能分组
└── demo/
    ├── overview/
    │   └── CaffeineOverviewDemo.java    # 核心概念
    ├── basic/
    │   └── BasicUsageDemo.java          # 基础使用
    ├── loading/
    │   └── LoadingCacheDemo.java        # 自动加载
    ├── eviction/
    │   ├── EvictionDemo.java            # 淘汰策略入口
    │   └── impl/
    │       ├── SizeBasedEviction.java   # 大小淘汰
    │       ├── WeightBasedEviction.java # 权重淘汰
    │       ├── TimeBasedEviction.java   # 时间淘汰
    │       ├── ReferenceEviction.java   # 引用淘汰
    │       └── RemovalListenerDemo.java # 淘汰监听器
    ├── stats/
    │   ├── StatsDemo.java               # 统计信息入口
    │   └── impl/
    │       ├── StatsEnableDemo.java     # 启用统计
    │       ├── StatsMetricsDemo.java    # 统计指标
    │       └── StatsMonitorDemo.java    # 监控方案
    └── async/
        └── AsyncCacheDemo.java          # 异步缓存

# 数据模型
└── model/
    └── UserData.java                    # 用户数据模型

# 测试目录
src/test/java/com/shuai/caffeine/
├── CaffeineDemoTest.java                # 主入口测试
├── config/
│   ├── CacheConfigsTest.java            # 配置测试
│   └── ExpiryConfigsTest.java           # 过期配置测试
├── util/
│   ├── SleepUtilsTest.java              # 工具测试
│   ├── DataLoaderTest.java              # 数据加载测试
│   └── DemoConstantsTest.java           # 常量测试
└── demo/basic/
    └── BasicUsageDemoTest.java          # 基础使用测试
```

### util 包说明

| 类 | 用途 |
|---|------|
| SleepUtils | 统一的 sleep 处理 |
| DataLoader | 模拟数据库/外部服务加载 |
| DemoConstants | 演示用的常量定义 |

### config 包说明

| 类 | 用途 |
|---|------|
| CacheConfigs | 预定义缓存配置模板 (smallCache, mediumCache, largeCache, weightedCache) |
| ExpiryConfigs | 预定义过期策略配置 (expireAfterWrite, expireAfterAccess, refreshAndExpire) |

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>com.github.benmanes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>3.1.8</version>
</dependency>
```

### 运行命令

```bash
mvn exec:java -Dexec.mainClass=com.shuai.caffeine.CaffeineDemo
```

## 核心内容

### 1. 核心概念

详细说明和代码示例请参考 [CaffeineOverviewDemo.java](src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java)。

| 知识点 | 方法 |
|--------|------|
| Caffeine 特性 | caffeineFeatures() |
| 四种缓存类型介绍 | cacheTypes() |
| 使用场景 | useCases() |

### 2. 基础使用

详细说明和代码示例请参考 [BasicUsageDemo.java](src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java)。

| 知识点 | 方法 |
|--------|------|
| Cache 接口创建 | cacheInterface() |
| 查询操作 (getIfPresent, get) | getOperations() |
| 写入操作 (put, putAll) | putOperations() |
| 删除操作 (invalidate) | invalidateOperations() |
| 批量操作 | batchOperations() |

### 3. LoadingCache 自动加载

详细说明和代码示例请参考 [LoadingCacheDemo.java](src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java)。

| 知识点 | 方法 |
|--------|------|
| LoadingCache 介绍 | loadingCacheIntro() |
| CacheLoader 使用 | cacheLoader() |
| 刷新策略 (refreshAfterWrite) | refreshPolicy() |
| 刷新 vs 过期对比 | refreshVsExpire() |

### 4. 淘汰策略

详细说明和代码示例请参考 [EvictionDemo.java](src/main/java/com/shuai/caffeine/demo/eviction/EvictionDemo.java) 及其 impl 子包。

| 知识点 | 文件 |
|--------|------|
| 基于大小的淘汰 (maximumSize) | SizeBasedEviction.java |
| 基于权重的淘汰 (maximumWeight) | WeightBasedEviction.java |
| 基于时间的淘汰 (expireAfterWrite/Access) | TimeBasedEviction.java |
| 基于引用的淘汰 (weak/soft) | ReferenceEviction.java |
| 淘汰监听器 (RemovalListener) | RemovalListenerDemo.java |

### 5. 统计信息

详细说明和代码示例请参考 [StatsDemo.java](src/main/java/com/shuai/caffeine/demo/stats/StatsDemo.java) 及其 impl 子包。

| 知识点 | 文件/方法 |
|--------|----------|
| 启用统计 (recordStats) | StatsEnableDemo.java |
| 常用统计指标 | StatsMetricsDemo.java |
| 命中率分析 | statsAnalysis() |
| 监控方案 | StatsMonitorDemo.java |
| 缓存健康检查 | cacheHealthCheck() |

### 6. 异步缓存

详细说明和代码示例请参考 [AsyncCacheDemo.java](src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java)。

| 知识点 | 方法 |
|--------|------|
| AsyncCache 介绍 | asyncCacheIntro() |
| 异步缓存操作 | asyncCacheOperations() |
| AsyncLoadingCache | asyncLoadingCache() |
| 异步 vs 同步对比 | asyncVsSync() |
| Executor 配置 | executorConfiguration() |

## 运行示例

执行以下命令运行所有演示：

```bash
mvn exec:java -Dexec.mainClass=com.shuai.caffeine.CaffeineDemo
```

## 系统学习文档

完整的系统学习文档请参考 [learn_docs/](learn_docs/) 目录：

| 章节 | 文档 | 内容 |
|------|------|------|
| 01 | [learn_docs/01-overview.md](learn_docs/01-overview.md) | Caffeine 概述、特性、与 Guava 对比 |
| 02 | [learn_docs/02-cache-types.md](learn_docs/02-cache-types.md) | 四种缓存类型详解 |
| 03 | [learn_docs/03-basic-usage.md](learn_docs/03-basic-usage.md) | 基础使用（get/put/invalidate） |
| 04 | [learn_docs/04-loading-cache.md](learn_docs/04-loading-cache.md) | LoadingCache 自动加载 |
| 05 | [learn_docs/05-eviction.md](learn_docs/05-eviction.md) | 淘汰策略（大小/时间/权重/引用） |
| 06 | [learn_docs/06-stats.md](learn_docs/06-stats.md) | 统计信息与监控 |
| 07 | [learn_docs/07-async-cache.md](learn_docs/07-async-cache.md) | 异步缓存 |
| 08 | [learn_docs/08-best-practices.md](learn_docs/08-best-practices.md) | 最佳实践 |
| 09 | [learn_docs/09-testing.md](learn_docs/09-testing.md) | 测试策略 |
| 10 | [learn_docs/10-llm-applications.md](learn_docs/10-llm-applications.md) | 大模型应用 |

**学习路径建议**：
1. 入门：先阅读 01-overview 了解 Caffeine 特性
2. 基础：学习 02-cache-types 和 03-basic-usage
3. 进阶：掌握 04-loading-cache 和 05-eviction
4. 高级：深入 06-stats 和 07-async-cache
5. 实践：参考 08-best-practices、09-testing 和 10-llm-applications

## 执行测试

```bash
mvn test                              # 运行所有测试 (36 tests)
mvn test -Dtest=*DemoTest            # 运行 Demo 测试
mvn test -Dtest=*UtilsTest           # 运行工具类测试
mvn test -Dtest=*ConfigsTest         # 运行配置类测试
```

**测试覆盖**：

| 包 | 测试类 | 测试数 |
|---|--------|--------|
| config/ | CacheConfigsTest, ExpiryConfigsTest | 14 |
| util/ | SleepUtilsTest, DataLoaderTest, DemoConstantsTest | 12 |
| demo/basic/ | BasicUsageDemoTest | 9 |
| 根目录 | CaffeineDemoTest | 1 |
| **总计** | | **36** |

## skills

```yaml
caffeine:
  cache_types:
    - Cache
    - LoadingCache
    - AsyncCache
    - AsyncLoadingCache
  eviction:
    - maximumSize
    - maximumWeight
    - weigher
    - expireAfterWrite
    - expireAfterAccess
    - expireAfter
    - refreshAfterWrite
    - weakKeys
    - weakValues
    - softValues
    - removalListener
  features:
    - recordStats
    - stats()
  async:
    - buildAsync
    - synchronous()
  spring_integration:
    - CaffeineCacheManager
    - @Cacheable
    - @CachePut
    - @CacheEvict
    - @Caching
```

---

**作者**: Shuai
**创建时间**: 2026-01-16
