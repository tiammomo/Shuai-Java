# 07 - 异步缓存

## 7.1 AsyncCache 介绍

AsyncCache 是 Caffeine 的异步缓存实现，返回 CompletableFuture，支持非阻塞操作。

```java
AsyncCache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(100)
    .expireAfterWrite(Duration.ofMinutes(5))
    .buildAsync();
```

详见 [AsyncCacheDemo.java:54-57](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L54-L57)

### 特点

| 特点 | 说明 |
|------|------|
| 非阻塞 | 返回 CompletableFuture |
| 高并发 | 适合大量并发请求 |
| 资源复用 | 线程池复用减少开销 |

---

## 7.2 异步缓存操作

### get(key, loader)

异步获取，返回 CompletableFuture

```java
CompletableFuture<String> future = cache.get("key1", key -> {
    SleepUtils.sleep(100);  // 模拟加载
    return "Value-" + key;
});

// 非阻塞处理结果
future.thenApply(value -> {
    // 处理数据
    return value;
});
```

详见 [AsyncCacheDemo.java:77-81](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L77-L81)

### getIfPresent(key)

异步查询

```java
CompletableFuture<String> future = cache.getIfPresent("key1");
```

详见 [AsyncCacheDemo.java:87-93](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L87-L93)

### put(key, value)

异步写入

```java
cache.put("key2", CompletableFuture.completedFuture("Value2"));
```

详见 [AsyncCacheDemo.java:97-98](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L97-L98)

### synchronous()

获取同步视图进行删除操作

```java
cache.synchronous().invalidate("key3");
```

详见 [AsyncCacheDemo.java:103-104](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L103-L104)

---

## 7.3 AsyncLoadingCache

异步自动加载缓存

```java
AsyncLoadingCache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(100)
    .buildAsync((key, executor) -> {
        return CompletableFuture.supplyAsync(() -> {
            SleepUtils.sleep(100);
            return "AsyncValue-" + key;
        }, executor);
    });
```

详见 [AsyncCacheDemo.java:142-150](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L142-L150)

### 批量加载 getAll()

```java
List<String> keys = Arrays.asList("a", "b", "c", "d", "e");
CompletableFuture<Map<String, String>> allFuture = cache.getAll(keys);
Map<String, String> results = allFuture.get();
```

详见 [AsyncCacheDemo.java:172-176](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L172-L176)

---

## 7.4 异步 vs 同步对比

| 特性 | 同步 (Cache) | 异步 (AsyncCache) |
|------|--------------|-------------------|
| get() 返回值 | 实际值 | CompletableFuture |
| 阻塞 | 阻塞调用线程 | 不阻塞 |
| 并发 | 线程等待 | 线程复用 |
| 适用 | 低并发 | 高并发 |

详见 [AsyncCacheDemo.java:183-219](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L183-L219)

### 性能对比

```
同步: 1000 并发 -> 线程阻塞
异步: 1000 并发 -> 复用少量线程
```

---

## 7.5 Executor 配置

### 自定义执行器

```java
ExecutorService executor = Executors.newFixedThreadPool(10);

AsyncCache<String, String> cache = Caffeine.newBuilder()
    .maximumSize(100)
    .buildAsync((key, exec) ->
        CompletableFuture.supplyAsync(() -> load(key), exec),
        executor
    );
```

详见 [AsyncCacheDemo.java:247-263](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L247-L263)

### 使用场景

| 场景 | 推荐配置 |
|------|----------|
| 数据库连接池控制 | 自定义线程池 |
| 远程 API 调用 | 自定义线程池 |
| 内存敏感 | ForkJoinPool.commonPool() |

---

## 7.6 代码位置汇总

| 功能 | 文件位置 |
|------|----------|
| AsyncCache 创建 | [AsyncCacheDemo.java:54-57](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L54-L57) |
| 异步操作 | [AsyncCacheDemo.java:77-104](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L77-L104) |
| AsyncLoadingCache | [AsyncCacheDemo.java:142-176](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L142-L176) |
| 异步 vs 同步 | [AsyncCacheDemo.java:183-219](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L183-L219) |
| Executor 配置 | [AsyncCacheDemo.java:247-263](../src/main/java/com/shuai/caffeine/demo/async/AsyncCacheDemo.java#L247-L263) |

---

## 7.7 实践要点

### 最佳实践

1. **IO 操作使用 AsyncCache** - 避免阻塞线程
2. **配置合理的线程池** - 根据任务特性调整
3. **处理 CompletableFuture 异常** - 避免异常丢失
4. **及时关闭线程池** - 生产环境需要管理

### 注意事项

- AsyncCache 默认使用 ForkJoinPool.commonPool()
- CompletableFuture.get() 会阻塞
- 需要处理线程池生命周期

---

## 7.8 课后练习

1. 运行 AsyncCacheDemo 查看异步效果
2. 对比同步和异步的性能差异
3. 为项目中的 IO 操作设计异步缓存

**下一章**: [08 - 最佳实践](08-best-practices.md)
