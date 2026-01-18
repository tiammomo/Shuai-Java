# 02 - 四种缓存类型

Caffeine 提供四种缓存类型，分别适用于不同场景。

## 2.1 Cache - 手动管理缓存

**适用场景**: 需要手动控制缓存生命周期的场景

```java
Cache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(100)
    .expireAfterWrite(Duration.ofMinutes(5))
    .build();
```

详见 [CaffeineOverviewDemo.java:93-97](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L93-L97)

**特点**:
- 手动调用 put/get/invalidate
- 不会自动加载数据
- 需要自己处理缓存未命中

---

## 2.2 LoadingCache - 自动加载缓存

**适用场景**: 需要缓存未命中时自动加载数据的场景

```java
LoadingCache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(1000)
    .build(key -> loadFromDb(key));
```

详见 [CaffeineOverviewDemo.java:99-103](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L99-L103)

**特点**:
- 通过 CacheLoader 自动加载
- get() 方法自动处理缓存未命中
- 避免缓存击穿

---

## 2.3 AsyncCache - 异步缓存

**适用场景**: 高并发场景，需要非阻塞操作

```java
AsyncCache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(1000)
    .buildAsync();
```

详见 [CaffeineOverviewDemo.java:106-108](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L106-L108)

**特点**:
- 返回 CompletableFuture
- 非阻塞式缓存操作
- 适合高并发场景

---

## 2.4 AsyncLoadingCache - 异步自动加载

**适用场景**: 需要异步加载 + 自动加载的场景

```java
AsyncLoadingCache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(1000)
    .buildAsync((key, executor) -> {
        return CompletableFuture.supplyAsync(() -> load(key), executor);
    });
```

详见 [CaffeineOverviewDemo.java:110-115](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L110-L115)

**特点**:
- 异步 + 自动加载
- 返回 CompletableFuture
- 支持自定义 Executor

---

## 2.5 缓存类型对比

| 类型 | 加载方式 | 返回值 | 适用场景 |
|------|----------|--------|----------|
| Cache | 手动 | 实际值 | 手动控制 |
| LoadingCache | 自动 | 实际值 | 数据库查询 |
| AsyncCache | 手动 | CompletableFuture | 高并发 |
| AsyncLoadingCache | 自动 | CompletableFuture | 异步查询 |

详见 [CaffeineOverviewDemo.java:91-120](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L91-L120)

---

## 2.6 代码位置汇总

| 功能 | 文件位置 |
|------|----------|
| 四种缓存类型定义 | [CaffeineOverviewDemo.java:91-120](../src/main/java/com/shuai/caffeine/demo/overview/CaffeineOverviewDemo.java#L91-L120) |
| Cache 使用 | [BasicUsageDemo.java](../src/main/java/com/shuai/caffeine/demo/basic/BasicUsageDemo.java) |
| LoadingCache 使用 | [LoadingCacheDemo.java](../src/main/java/com/shuai/caffeine/demo/loading/LoadingCacheDemo.java) |
| AsyncCache 使用 | [AsyncCacheDemo.java](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java) |

---

## 2.7 实践要点

### 如何选择缓存类型

1. **简单场景** -> 使用 Cache
2. **需要自动加载** -> 使用 LoadingCache
3. **高并发场景** -> 使用 AsyncCache/AsyncLoadingCache
4. **IO 密集型** -> 使用 AsyncLoadingCache

### 注意事项

- LoadingCache 的 get() 是阻塞的
- AsyncCache 需要处理 CompletableFuture
- AsyncLoadingCache 需要注意线程池配置

---

## 2.8 课后练习

1. 对比四种缓存类型的 API 差异
2. 编写代码测试不同类型的性能
3. 分析项目中各场景适合使用哪种类型

**下一章**: [03 - 基础使用](03-basic-usage.md)
